@file:Suppress("DEPRECATION")

package com.example.btlogin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke // Đã thêm import cho BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.btlogin.ui.theme.BTLogInTheme

// --- Firebase/Google Sign-in Imports ---
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

// --- Data Model và Routes giữ nguyên ---
data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val dob: String = "14/09/2005"
)

fun FirebaseUser.toAppUser(): User {
    return User(
        id = this.uid,
        name = this.displayName ?: "Unknown User",
        email = this.email ?: "N/A",
        photoUrl = this.photoUrl?.toString()
    )
}

sealed class Screen(val route: String) {
    object Login : Screen("login_flow")
    object Profile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            BTLogInTheme {
                val currentUser = remember { mutableStateOf(firebaseAuth.currentUser?.toAppUser()) }
                AppNavigation(currentUser, googleSignInClient, firebaseAuth)
            }
        }
    }
}

// --- Component Navigation Chính ---
@Composable
fun AppNavigation(
    currentUser: MutableState<User?>,
    googleSignInClient: GoogleSignInClient,
    firebaseAuth: FirebaseAuth
) {
    val navController = rememberNavController()

    val startDestination = if (currentUser.value != null) {
        Screen.Profile.createRoute(currentUser.value!!.id)
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                googleSignInClient = googleSignInClient,
                firebaseAuth = firebaseAuth,
                onLoginSuccess = { fUser ->
                    val user = fUser.toAppUser()
                    currentUser.value = user
                    navController.navigate(Screen.Profile.createRoute(user.id)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            val user = currentUser.value ?: firebaseAuth.currentUser?.toAppUser()

            if (user != null) {
                ProfileScreen(
                    user = user,
                    onBack = { navController.popBackStack() }
                )
            } else {
                navController.navigate(Screen.Login.route)
            }
        }
    }
}

// KHÔNG ĐẶT @Preview Ở ĐÂY NỮA!
@Composable
fun LoginScreen(
    googleSignInClient: GoogleSignInClient,
    firebaseAuth: FirebaseAuth,
    onLoginSuccess: (FirebaseUser) -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        onLoginSuccess(firebaseAuth.currentUser!!)
                        Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Firebase Auth thất bại: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In thất bại. Mã lỗi: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Header & Logo
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BÀI TẬP VỀ NHÀ",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Divider(color = Color.Red, thickness = 2.dp, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(64.dp))

            // Đã sửa thành tên dùng tiền tố 'ic_'
            Image(
                painter = painterResource(id = R.drawable.uth_logo),
                contentDescription = "UTH Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("UTH", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("SmartTasks", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("A simple and efficient to-do app", fontSize = 12.sp, color = Color.Gray)
        }

        // 2. Welcome Message & Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Ready to explore? Log in to get started.", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { launcher.launch(googleSignInClient.signInIntent) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(width = 1.dp, color = Color.Gray), // Cú pháp đúng
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Đã sửa thành tên dùng tiền tố 'ic_'
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "G SIGN IN WITH GOOGLE",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 3. Footer
        Text("© UTH SmartTasks", fontSize = 10.sp, color = Color.Gray)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: User, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                actions = {
                    IconButton(onClick = { /* TODO: Logout/Settings */ }) {
                        Icon(painterResource(id = R.drawable.ic_settings), contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AsyncImage(
                model = user.photoUrl,
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                error = painterResource(id = R.drawable.ic_profile_placeholder),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Thông tin chi tiết
            ProfileInfoRow(label = "Name", value = user.name)
            ProfileInfoRow(label = "Email", value = user.email)
            ProfileInfoRow(label = "Date of Birth", value = user.dob)

            Spacer(modifier = Modifier.weight(1f))

            // 3. Nút Back
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text("Back", modifier = Modifier.padding(vertical = 8.dp), fontSize = 18.sp)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: User, onBack: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current

    // [LOGOUT] Thêm logic hiển thị menu khi nhấn nút settings
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                actions = {
                    IconButton(onClick = { showMenu = true }) { // [LOGOUT] Mở menu khi nhấn
                        Icon(painterResource(id = R.drawable.ic_settings), contentDescription = "Settings")
                    }

                    // [LOGOUT] Dropdown Menu cho chức năng Logout
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout() // Gọi hành động đăng xuất
                                Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AsyncImage(
                model = user.photoUrl,
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                error = painterResource(id = R.drawable.ic_profile_placeholder),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Thông tin chi tiết
            ProfileInfoRow(label = "Name", value = user.name)
            ProfileInfoRow(label = "Email", value = user.email)
            ProfileInfoRow(label = "Date of Birth", value = user.dob)

            Spacer(modifier = Modifier.weight(1f))

            // 3. Nút Back (giữ nguyên)
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text("Back", modifier = Modifier.padding(vertical = 8.dp), fontSize = 18.sp)
            }
        }
    }
}
// --- Component cho mỗi dòng thông tin (Giữ nguyên) ---
@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.Black, fontSize = 16.sp)
        Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(top = 4.dp))
    }
}

// --- PHẦN PREVIEW ĐÃ ĐƯỢC CHUYỂN VÀ SỬA LỖI ---

@Preview(showBackground = true, name = "1. Login Screen Preview")
@Composable
fun LoginScreenPreview() {
    val context = LocalContext.current

    // Giả lập các tham số phức tạp cho Preview
    val dummyGoogleClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    val dummyFirebaseAuth = FirebaseAuth.getInstance()

    BTLogInTheme {
        LoginScreen(
            googleSignInClient = dummyGoogleClient,
            firebaseAuth = dummyFirebaseAuth,
            onLoginSuccess = {} // Chỉ là hàm rỗng cho Preview
        )
    }
}

@Preview(showBackground = true, name = "2. Profile Screen Preview")
@Composable
fun ProfileScreenPreview() {
    // Dữ liệu giả lập (Mock Data)
    val dummyUser = User(
        id = "preview_user",
        name = "Melisa Peters",
        email = "melpet@gmail.com",
        photoUrl = "https://i.pravatar.cc/300?img=4" // Sử dụng URL giả
    )
    BTLogInTheme {
        ProfileScreen(dummyUser, onBack = {})
    }
}