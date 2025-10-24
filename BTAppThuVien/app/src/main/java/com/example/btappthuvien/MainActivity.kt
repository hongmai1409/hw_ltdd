package com.example.btappthuvien

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
// Các import Material 3 cần thiết
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.btappthuvien.ui.theme.BTAppThuVienTheme
import java.util.UUID

// ----------------------------------------------------------------------
// 1. Data Class, Tuyến (Routes)
// ----------------------------------------------------------------------

data class Book(val id: String = UUID.randomUUID().toString(), val name: String)
data class Student(val id: String = UUID.randomUUID().toString(), val name: String)
data class BorrowRecord(val id: String = UUID.randomUUID().toString(), val studentId: String, val bookId: String)


object NavRoutes {
    const val LIBRARY_MANAGEMENT = "quan_ly"
    const val BOOK_LIST = "ds_sach"
    const val STUDENTS = "sinh_vien"
}

// ----------------------------------------------------------------------
// 2. Các Composable Thành phần (Giữ nguyên)
// ----------------------------------------------------------------------

@Composable
fun CustomCheckboxListItem(
    bookName: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // ... (Giữ nguyên code CustomCheckboxListItem)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val borderColor = Color(0xFFD3D3D3)

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(4.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF007AFF),
                    uncheckedColor = borderColor,
                    checkmarkColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = bookName,
                style = TextStyle(fontSize = 16.sp, color = Color.Black)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoSection(
    studentName: String,
    onStudentNameChange: (String) -> Unit,
    isEditing: Boolean,
    onEditToggle: () -> Unit
) {
    // ... (Giữ nguyên code UserInfoSection)
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(
            text = "Sinh viên",
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = studentName,
                onValueChange = onStudentNameChange,
                enabled = isEditing,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF007AFF),
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledTextColor = Color.Black,
                    cursorColor = Color(0xFF007AFF)
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onEditToggle,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(90.dp).height(48.dp)
            ) {
                Text(
                    text = if (isEditing) "Lưu" else "Thay đổi",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ----------------------------------------------------------------------
// 3. Màn hình Quản lý (Đã sửa đổi logic)
// ----------------------------------------------------------------------

@Composable
fun LibraryManagementScreen(
    currentStudent: Student?, // Sinh viên đang được chọn
    books: List<Book>,
    checkedBookStates: MutableMap<String, Boolean>,
    onBorrow: () -> Unit,
    onSelectOrAddStudent: (String) -> Unit // Hàm xử lý khi nhấn "Lưu"
) {
    // Trạng thái cho TextField
    var studentNameInField by remember { mutableStateOf(currentStudent?.name ?: "") }
    // Trạng thái chỉnh sửa
    var isEditing by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Cập nhật TextField nếu currentStudent thay đổi (từ MainScreen)
    LaunchedEffect(currentStudent) {
        studentNameInField = currentStudent?.name ?: ""
    }

    // Logic xử lý nút "Mượn Sách"
    val onAddClick = {
        val anyBookSelected = checkedBookStates.containsValue(true)
        if (anyBookSelected) {
            onBorrow()
            showError = false
        } else {
            showError = true
        }
    }

    // Logic xử lý nút "Thay đổi / Lưu"
    val onEditToggle = {
        if (isEditing) {
            // Nếu đang chỉnh sửa -> nhấn LƯU
            if (studentNameInField.isNotBlank()) {
                onSelectOrAddStudent(studentNameInField)
            }
        }
        isEditing = !isEditing // Bật/tắt chế độ chỉnh sửa
        if (!isEditing) {
            showError = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Hệ thống\nQuản lý Thư viện",
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp)
        )

        UserInfoSection(
            studentName = studentNameInField,
            onStudentNameChange = { studentNameInField = it },
            isEditing = isEditing,
            onEditToggle = onEditToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Danh sách sách (Sẵn có để mượn)",
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (books.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sinh viên này đã mượn hết sách có sẵn.\n(Hãy qua tab 'DS Sách' để thêm)",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn {
                    items(books, key = { it.id }) { book ->
                        val isChecked = checkedBookStates[book.id] ?: false

                        CustomCheckboxListItem(
                            bookName = book.name,
                            isChecked = isChecked,
                            onCheckedChange = { newCheckState ->
                                checkedBookStates[book.id] = newCheckState
                            }
                        )
                    }
                }
            }
        }

        if (showError) {
            Text(
                text = "Vui lòng chọn ít nhất một cuốn sách để mượn",
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            enabled = currentStudent != null // Chỉ cho phép mượn khi đã có sinh viên được chọn
        ) {
            Text(text = "Mượn Sách", color = Color.White, fontSize = 20.sp)
        }
    }
}

// ----------------------------------------------------------------------
// 4. Màn hình Danh sách Sách (Giữ nguyên)
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    books: List<Book>,
    onAddBook: (String) -> Unit
) {
    var newBookName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý Sách",
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Thêm Sách Mới",
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newBookName,
                    onValueChange = { newBookName = it },
                    label = { Text("Tên sách") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color(0xFF007AFF),
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color.Black,
                        cursorColor = Color(0xFF007AFF)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newBookName.isNotBlank()) {
                            onAddBook(newBookName)
                            newBookName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Thêm", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sách hiện có trong thư viện",
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(books, key = { it.id }) { book ->
                    BookListItem(bookName = book.name)
                }
            }
        }
    }
}

// Composable phụ để hiển thị một mục sách
@Composable
fun BookListItem(bookName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = bookName,
            style = TextStyle(fontSize = 16.sp, color = Color.Black)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}


// ----------------------------------------------------------------------
// 5. Màn hình Sinh viên (Đã sửa đổi)
// ----------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    students: List<Student>,
    books: List<Book>,
    records: SnapshotStateList<BorrowRecord>,
    onReturnBook: (String) -> Unit,
    onAddStudent: (String) -> Unit // <-- Hàm mới
) {
    // Trạng thái cho TextField thêm sinh viên
    var newStudentName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý Sinh viên",
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            // ----- Giao diện Thêm Sinh viên -----
            Text(
                text = "Thêm Sinh viên Mới",
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newStudentName,
                    onValueChange = { newStudentName = it },
                    label = { Text("Tên sinh viên") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color(0xFF007AFF),
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color.Black,
                        cursorColor = Color(0xFF007AFF)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newStudentName.isNotBlank()) {
                            onAddStudent(newStudentName) // Gọi hàm thêm SV
                            newStudentName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Thêm", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            // ----- Kết thúc Giao diện Thêm -----


            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(students, key = { it.id }) { student ->
                    val studentRecords = records.filter { it.studentId == student.id }
                    val borrowedItems = studentRecords.mapNotNull { record ->
                        val book = books.find { it.id == record.bookId }
                        if (book != null) {
                            Pair(record, book)
                        } else {
                            null
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Số lượng sách đã mượn: ${borrowedItems.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (borrowedItems.isEmpty()) {
                            Text(
                                text = "Chưa mượn sách nào.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        } else {
                            borrowedItems.forEach { (record, book) ->
                                StudentBookItem(
                                    bookName = book.name,
                                    onReturnClick = {
                                        onReturnBook(record.id)
                                    }
                                )
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// Composable phụ cho từng cuốn sách sinh viên mượn
@Composable
fun StudentBookItem(
    bookName: String,
    onReturnClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = bookName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(
            onClick = onReturnClick,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Trả sách")
        }
    }
}


// ----------------------------------------------------------------------
// 6. Navigation (Giữ nguyên)
// ----------------------------------------------------------------------

@Composable
fun BottomNavigationBarWithNav(
    navController: NavHostController,
    currentRoute: String?
) {
    // ... (Giữ nguyên code BottomNavigationBarWithNav)
    val items = listOf(
        Pair(NavRoutes.LIBRARY_MANAGEMENT, "Quản lý"),
        Pair(NavRoutes.BOOK_LIST, "DS Sách"),
        Pair(NavRoutes.STUDENTS, "Sinh viên")
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray,
        modifier = Modifier.height(56.dp)
    ) {
        val selectedColor = Color(0xFF007AFF)

        items.forEach { (route, label) ->
            val isSelected = currentRoute == route
            val icon = when (route) {
                NavRoutes.LIBRARY_MANAGEMENT -> Icons.Default.Home
                NavRoutes.BOOK_LIST -> Icons.Default.List
                NavRoutes.STUDENTS -> Icons.Default.AccountCircle
                else -> Icons.Default.Home
            }

            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                selected = isSelected,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    indicatorColor = Color.White
                )
            )
        }
    }
}

// ----------------------------------------------------------------------
// 7. SỬA ĐỔI: MainScreen (Logic chọn/thêm sinh viên VÀ RESET CHECKBOX)
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ----- NÂNG TRẠNG THÁI (LIFT STATE) -----

    val books = remember {
        mutableStateListOf(
            Book(id="b1", name = "Sách 01 (Có sẵn)"),
            Book(id="b2", name = "Sách 02 (Có sẵn)")
        )
    }

    val students = remember {
        mutableStateListOf(
            Student(id = "sv1", name = "Nguyen Van A"),
            Student(id = "sv2", name = "Tran Thi B")
        )
    }

    // ID của sinh viên đang được chọn ở trang chủ
    var currentStudentId by remember { mutableStateOf(students.firstOrNull()?.id) }
    // Đối tượng Student tương ứng
    val currentStudent = students.find { it.id == currentStudentId }

    val borrowRecords = remember { mutableStateListOf<BorrowRecord>() }
    val checkedBookStates = remember { mutableStateMapOf<String, Boolean>() }

    // Hàm Thêm Sách
    val onAddBook: (String) -> Unit = { bookName ->
        books.add(Book(name = bookName))
    }

    // Hàm Trả Sách
    val onReturnBook: (String) -> Unit = { recordId ->
        borrowRecords.removeIf { it.id == recordId }
    }

    // Hàm Mượn Sách
    val onBorrowBook: (String, MutableMap<String, Boolean>) -> Unit = { studentId, checkedStates ->
        checkedStates.forEach { (bookId, isChecked) ->
            if (isChecked) {
                val alreadyBorrowed = borrowRecords.any { it.studentId == studentId && it.bookId == bookId }
                if (!alreadyBorrowed) {
                    borrowRecords.add(BorrowRecord(studentId = studentId, bookId = bookId))
                }
            }
        }
        checkedStates.clear()
    }

    // Hàm Thêm Sinh viên (cho StudentScreen)
    val onAddStudent: (String) -> Unit = { name ->
        val existingStudent = students.find { it.name.equals(name, ignoreCase = true) }
        if (existingStudent == null && name.isNotBlank()) {
            students.add(Student(name = name))
        }
    }

    // SỬA ĐỔI CHÍNH: Hàm Chọn/Thêm Sinh viên (cho LibraryManagementScreen)
    val onSelectOrAddStudent: (String) -> Unit = { name ->
        val existingStudent = students.find { it.name.equals(name, ignoreCase = true) }
        val newStudentId: String

        if (existingStudent != null) {
            // Nếu đã tồn tại, chỉ cần chọn
            newStudentId = existingStudent.id
        } else {
            // Nếu chưa tồn tại, thêm mới và chọn
            val newStudent = Student(name = name)
            students.add(newStudent) // <-- Tự động cập nhật trang Sinh viên
            newStudentId = newStudent.id
        }

        // Chỉ reset nếu ID sinh viên thay đổi
        if (currentStudentId != newStudentId) {
            currentStudentId = newStudentId
            // YÊU CẦU 1: Reset lại các sách đã check
            checkedBookStates.clear()
        }
    }

    // ----- KẾT THÚC NÂNG TRẠNG THÁI -----

    Scaffold(
        bottomBar = {
            BottomNavigationBarWithNav(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.LIBRARY_MANAGEMENT,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavRoutes.LIBRARY_MANAGEMENT) {
                LibraryManagementScreen(
                    currentStudent = currentStudent,
                    books = books.filter { book ->
                        currentStudent == null || borrowRecords.none { it.studentId == currentStudent.id && it.bookId == book.id }
                    },
                    checkedBookStates = checkedBookStates,
                    onBorrow = {
                        if (currentStudent != null) {
                            onBorrowBook(currentStudent.id, checkedBookStates)
                        }
                    },
                    onSelectOrAddStudent = onSelectOrAddStudent
                )
            }
            composable(NavRoutes.BOOK_LIST) {
                BookListScreen(
                    books = books,
                    onAddBook = onAddBook
                )
            }
            composable(NavRoutes.STUDENTS) {
                StudentScreen(
                    students = students,
                    books = books,
                    records = borrowRecords,
                    onReturnBook = onReturnBook,
                    onAddStudent = onAddStudent // Truyền hàm thêm SV
                )
            }
        }
    }
}

// ----------------------------------------------------------------------
// 8. MainActivity và Preview
// ----------------------------------------------------------------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BTAppThuVienTheme {
                MainScreen()
            }
        }
    }
}

// Sửa đổi Preview
@Preview(showBackground = true, device = "spec:width=360dp,height=800dp,dpi=480")
@Composable
fun PreviewMainScreen() {
    BTAppThuVienTheme {
        val student = Student(id="sv1", name = "Nguyen Van A")
        val books = remember { mutableStateListOf(Book(name = "Sách 01"), Book(name = "Sách 02")) }
        val checks = remember { mutableStateMapOf("1" to true) }
        LibraryManagementScreen(
            currentStudent = student,
            books = books,
            checkedBookStates = checks,
            onBorrow = {},
            onSelectOrAddStudent = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=800dp,dpi=480")
@Composable
fun PreviewBookListScreen() {
    BTAppThuVienTheme {
        val books = remember { mutableStateListOf(Book(name = "Sách 01")) }
        BookListScreen(books = books, onAddBook = {})
    }
}

// Sửa đổi Preview
@Preview(showBackground = true, device = "spec:width=360dp,height=800dp,dpi=480")
@Composable
fun PreviewStudentScreen() {
    BTAppThuVienTheme {
        val students = remember { mutableStateListOf(Student(id = "sv1", name = "Nguyen Van A")) }
        val books = remember { mutableStateListOf(Book(id="b1", name = "Sách Lập Trình Kotlin")) }
        val records = remember { mutableStateListOf(BorrowRecord(id="r1", studentId="sv1", bookId="b1")) }
        StudentScreen(
            students = students,
            books = books,
            records = records,
            onReturnBook = {},
            onAddStudent = {}
        )
    }
}