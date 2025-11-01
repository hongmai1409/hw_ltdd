@file:Suppress("DEPRECATION")

package com.example.btlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.btlogin.ui.theme.BTLogInTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Firebase/Google Sign-in Imports ---
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.facebook.CallbackManager // [FB]
import com.facebook.FacebookCallback // [FB]
import com.facebook.FacebookException // [FB]
import com.facebook.login.LoginManager // [FB]
import com.facebook.login.LoginResult // [FB]

private const val AUTO_LOGOUT_DELAY_MS = 5 * 60 * 1000L
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
    private var logoutJob: Job? = null

    lateinit var callbackManager: CallbackManager // [FB] Khai báo Callback Manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [FB] Khởi tạo Facebook Callback Manager
        callbackManager = CallbackManager.Factory.create()
        // Đảm bảo đăng xuất các phiên Facebook cũ khi khởi động
        LoginManager.getInstance().logOut()

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
                AutoLogoutLifecycleObserver(
                    currentUser = currentUser,
                    onTimeout = {
                        // Gọi hàm handleLogout (sẽ tự động đăng xuất Facebook)
                        handleLogout(currentUser)
                        Toast.makeText(this, "Tự động đăng xuất sau 5 phút không hoạt động.", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleLogout(currentUser: MutableState<User?>) {
        if (currentUser.value != null) {
            currentUser.value = null
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            LoginManager.getInstance().logOut() // [FB] Đăng xuất khỏi Facebook
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }
        stopLogoutTimer()
    }

    private fun startLogoutTimer() {
        if (firebaseAuth.currentUser != null && logoutJob == null) {
            logoutJob = lifecycleScope.launch {
                delay(AUTO_LOGOUT_DELAY_MS)
                handleLogout(mutableStateOf(firebaseAuth.currentUser?.toAppUser()))
            }
        }
    }

    private fun stopLogoutTimer() {
        logoutJob?.cancel()
        logoutJob = null
    }

    @SuppressLint("ContextCastToActivity")
    @Composable
    private fun AutoLogoutLifecycleObserver(currentUser: State<User?>, onTimeout: () -> Unit) {
        val lifecycleOwner = LocalContext.current as ComponentActivity

        DisposableEffect(lifecycleOwner.lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        if (currentUser.value != null) {
                            startLogoutTimer()
                        }
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        stopLogoutTimer()
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                stopLogoutTimer()
            }
        }
    }
}

@SuppressLint("ContextCastToActivity")
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

    val activity = LocalContext.current as MainActivity
    val callbackManager = activity.callbackManager // [FB] Lấy CallbackManager từ Activity

    val onLogout: () -> Unit = {
        currentUser.value = null
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        LoginManager.getInstance().logOut() // [FB] Đăng xuất khỏi Facebook

        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                googleSignInClient = googleSignInClient,
                firebaseAuth = firebaseAuth,
                callbackManager = callbackManager, // [FB] Truyền CallbackManager vào LoginScreen
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
                    onBack = { navController.popBackStack() },
                    onLogout = onLogout
                )
            } else {
                navController.navigate(Screen.Login.route)
            }
        }
    }
}

@Composable
fun LoginScreen(
    googleSignInClient: GoogleSignInClient,
    firebaseAuth: FirebaseAuth,
    callbackManager: CallbackManager, // [FB] Tham số mới
    onLoginSuccess: (FirebaseUser) -> Unit
) {
    val context = LocalContext.current
    val loginManager = LoginManager.getInstance() // [FB] Lấy LoginManager

    // [FB] Khởi tạo logic Facebook Sign-in
    DisposableEffect(key1 = Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // Đổi Facebook Access Token thành Firebase Credential
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            onLoginSuccess(firebaseAuth.currentUser!!)
                            Toast.makeText(context, "Đăng nhập FB thành công!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Firebase/FB Auth thất bại: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
            override fun onCancel() {
                Toast.makeText(context, "Đăng nhập Facebook bị hủy.", Toast.LENGTH_SHORT).show()
            }
            override fun onError(error: FacebookException) {
                Toast.makeText(context, "Lỗi Facebook: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
        onDispose {
            // Dọn dẹp
        }
    }

    // Logic Google Sign-in giữ nguyên
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        onLoginSuccess(firebaseAuth.currentUser!!)
                        Toast.makeText(context, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // THIẾU TEXT "BÀI TẬP VỀ NHÀ" ở đây, tôi giữ nguyên như code cũ
            Divider(color = Color.Red, thickness = 2.dp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(64.dp))
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

        // 2. Welcome Message & Buttons
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Ready to explore? Log in to get started.", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            // Nút 1: Đăng nhập Google (Giữ nguyên)
            Button(
                onClick = { launcher.launch(googleSignInClient.signInIntent) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(width = 1.dp, color = Color.Gray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
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

            Spacer(modifier = Modifier.height(12.dp))

            // [FB] Nút 2: Đăng nhập Facebook
            Button(
                // Yêu cầu quyền email và public_profile
                onClick = { loginManager.logInWithReadPermissions(context as ComponentActivity, listOf("email", "public_profile")) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)), // Màu xanh Facebook
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // [FB] Giả định có drawable R.drawable.ic_facebook_logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook_logo),
                        contentDescription = "Facebook Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "SIGN IN WITH FACEBOOK",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 3. Footer
        Text("© UTH SmartTasks", fontSize = 10.sp, color = Color.Gray)
    }
}

// --- Màn hình ProfileScreen và các hàm phụ giữ nguyên ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: User, onBack: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(painterResource(id = R.drawable.ic_settings), contentDescription = "Settings")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
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

// --- PHẦN PREVIEW (Cập nhật tham số LoginScreenPreview) ---

@SuppressLint("ContextCastToActivity")
@Preview(showBackground = true, name = "1. Login Screen Preview")
@Composable
fun LoginScreenPreview() {
    val context = LocalContext.current as MainActivity // Cast về MainActivity để lấy CallbackManager

    val dummyGoogleClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    val dummyFirebaseAuth = FirebaseAuth.getInstance()

    // Tạo CallbackManager giả lập
    val dummyCallbackManager = remember { CallbackManager.Factory.create() }

    BTLogInTheme {
        LoginScreen(
            googleSignInClient = dummyGoogleClient,
            firebaseAuth = dummyFirebaseAuth,
            callbackManager = dummyCallbackManager, // Truyền vào
            onLoginSuccess = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Profile Screen Preview")
@Composable
fun ProfileScreenPreview() {
    val dummyUser = User(
        id = "preview_user",
        name = "Melisa Peters",
        email = "melpet@gmail.com",
        photoUrl = "https://i.pravatar.cc/300?img=4"
    )
    BTLogInTheme {
        ProfileScreen(dummyUser, onBack = {}, onLogout = {})
    }
}