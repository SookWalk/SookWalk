package com.example.sookwalk.presentation.screens.member

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageEditScreen(
    viewModel: UserViewModel,
    navController: NavController
    ) {

    // Firebase에서 현재 유저의 uid를 가져온다
    val uid = Firebase.auth.currentUser?.uid

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // 로컬 화면에 띄울 이미지 Uri
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 프로필 사진의 삭제 여부 저장
    var isProfileImageDeleted by remember { mutableStateOf(false) }

    // firebase storage에서 '기존에' 불러온 프로필 이미지 URL
    var profileImageUrlFromStorage by remember { mutableStateOf<String?>(null) }

    // 갤러리에서 콘텐츠를 가져오는 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { imageUri ->
                // 우선 ui에 선택 이미지를 띄운다
                selectedImageUri = imageUri
                // 바텀 시트 닫기
                showBottomSheet = false
            }
        }
    )

    // 2. 화면 시작 시, Storage에서 이미지 URL 가져오기
    // LaunchedEffect(true)는 이 Composable이 처음 Composition될 때 딱 한 번만 실행됩니다.
    LaunchedEffect(key1 = true) {
        if (uid != null) {
            // 하위 위치를 나타내는 참조 생성
            val storageRef = Firebase.storage.reference.child("images/$uid/profile.jpg")
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    // URL을 성공적으로 가져오면 상태에 저장
                    profileImageUrlFromStorage = uri.toString()
                }
                .addOnFailureListener { exception ->
                    // --- 여기가 바로 수정된 부분입니다 ---
                    // 실패 원인을 확인하여, '파일이 없는 경우'는 정상적인 케이스로 간주합니다.
                    if (exception is com.google.firebase.storage.StorageException &&
                        exception.errorCode == com.google.firebase.storage.StorageException.ERROR_OBJECT_NOT_FOUND
                    ) {
                        // 프로필 사진이 원래 없는 경우 (예: 신규 가입자)
                        // 이것은 오류가 아니므로, 조용히 null로 처리
                        Log.d("MyPageEdit", "프로필 이미지가 스토리지에 존재하지 않습니다. (정상 케이스)")
                    } else {
                        Log.d("MyPageEditScreen", "기존 프로필 이미지를 불러오는 데 실패했습니다.")
                    }
                    profileImageUrlFromStorage = null
                }
        }
    }

    // 권한을 요청하는 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용되면 갤러리 런처를 실행
                galleryLauncher.launch("image/*")
            } else {
                // 권한이 거부되면 바텀 시트를 닫음
                showBottomSheet = false
            }
        }
    )

    // 안드로이드 버전에 따라 필요한 권한을 정의
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var nickname by remember { mutableStateOf("") }
    var isNicknameAvailable by remember { mutableStateOf<Boolean?>(null) }
    var isAvailableNicknameMsg by remember { mutableStateOf("") }
    var hasCheckedNickname by remember { mutableStateOf(false) }

    var major by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var isChangedMajor by remember { mutableStateOf(false) }


    val departments = listOf(
        "IP·콘텐츠전공", "IT공학전공", "K-POP산업경영전공", "게임콘텐츠디자인전공", "공공인재학전공", "과학저널리즘전공"
    )

    val filtered = remember(major) {
        if (major.isBlank()) departments else departments.filter {
            it.contains(
                major,
                ignoreCase = true
            )
        }
    }

    Scaffold(
        topBar = {
            TopBar(screenName = "마이페이지",
                { navController.popBackStack() },
                {navController.navigate("alarm")},
                {}) }

    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(innerPadding)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(
                                    selectedImageUri // 1. 새로 선택한 이미지를 최우선
                                        ?: if (!isProfileImageDeleted) {
                                            profileImageUrlFromStorage // 2. Storage 이미지
                                        } else {
                                            null // 3. 삭제되었으면 null -> placeholder 보여주기
                                        }
                                        ?: R.drawable.default_profile_image // 4. 조건 모두 불만족, 기본 이미지
                                ).crossfade(true).build(),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .clip(CircleShape)
                                .clickable { showBottomSheet = true },
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.default_profile_image)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "수정 버튼",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-16).dp, y = (-16).dp)
                                .clip(CircleShape)
                                .clickable { showBottomSheet = true }
                                .background(MaterialTheme.colorScheme.tertiary)
                                .padding(12.dp),
                            tint = Color.White
                        )
                    }
                }
                // ... (닉네임, 학과 수정 등 나머지 코드는 동일)
                item { // 닉네임 수정
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            "닉네임",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TextField(
                            value = nickname, onValueChange = { nickname = it },
                            placeholder = { Text("변경할 닉네임을 입력하세요") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 4.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.DarkGray
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = isAvailableNicknameMsg,
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall
                            )


                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.isNicknameAvailable(nickname)

                                    if (viewModel.isAvailableNickname.value) {
                                        isAvailableNicknameMsg = "사용 가능한 아이디입니다."
                                        isNicknameAvailable = true
                                    } else {
                                        isAvailableNicknameMsg = "이미 존재하는 아이디입니다."
                                    }
                                },
                                shape = RoundedCornerShape(28),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("중복 확인", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row { Text("소속 학부 ", style = MaterialTheme.typography.bodyLarge) }
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(modifier = Modifier.padding(8.dp)) {
                            TextField(
                                value = major, onValueChange = { major = it; expanded = true },
                                placeholder = { Text("소속 학부를 입력하세요") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "검색 아이콘"
                                    )
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(
                                        0xFFF4F4F4
                                    ),
                                    focusedContainerColor = Color(0xFFF4F4F4),
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.DarkGray
                                )
                            )
                            if (expanded && filtered.isNotEmpty()) {
                                Column {
                                    filtered.forEach { dept ->
                                        val annotated = buildAnnotatedString {
                                            val startIndex = dept.indexOf(major, ignoreCase = true)
                                            if (startIndex >= 0) {
                                                val endIndex = startIndex + major.length
                                                append(dept.substring(0, startIndex))
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append(
                                                        dept.substring(startIndex, endIndex)
                                                    )
                                                }
                                                append(dept.substring(endIndex))
                                            } else append(dept)
                                        }
                                        Text(
                                            text = annotated,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { major = dept; expanded = false }
                                                .padding(vertical = 8.dp, horizontal = 12.dp),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // -------------  프로필 이미지 관련 --------------
                                // 새 이미지가 업로드 된 경우, 업로드 실행
                                selectedImageUri?.let { imageUri ->
                                    uploadImageToFirebase(
                                        imageUri = imageUri,
                                        onSuccess = { downloadUrl ->
                                            Log.d("UpdateProfile", "이미지 업로드 성공: $downloadUrl")
                                        },
                                        onFailure = { exception ->
                                            Log.e("UpdateProfile", "이미지 업로드 실패", exception)
                                        }
                                    )
                                }

                                // 이미지가 삭제된 경우
                                if (isProfileImageDeleted) {
                                    deleteImageFromFirebase(
                                        onSuccess = {
                                            Log.d("deleteProfile", "Firebase Storage에서 이미지 삭제 성공")
                                        },
                                        onFailure = { exception ->
                                            // 파일이 원래 없어서 발생하는 오류는 무시해도 괜찮습니다.
                                            if (exception is com.google.firebase.storage.StorageException &&
                                                exception.errorCode == com.google.firebase.storage.StorageException.ERROR_OBJECT_NOT_FOUND
                                            ) {
                                                Log.d("UpdateProfile", "삭제할 이미지가 스토리지에 원래 없었음.")
                                                // 이 경우에도 DB의 URL은 삭제 처리해야 하므로, 성공 로직을 태울 수 있습니다.
                                                // TODO: ViewModel을 통해 서버 DB의 이미지 URL 필드를 null로 업데이트
                                            } else {
                                                Log.e(
                                                    "UpdateProfile",
                                                    "Firebase Storage 이미지 삭제 실패",
                                                    exception
                                                )
                                            }
                                        })
                                }

                                // ------------ 닉네임, 학과 관련 -------------
                                if (isNicknameAvailable ?: false || isChangedMajor) {
                                    viewModel.updateNicknameAndMajor(nickname, major) // 룸DB에 저장
                                    Firebase.firestore.collection("users").document(uid?:"").update(mapOf(
                                        "nickname" to nickname,
                                        "major" to major )
                                    )
                                }
                                /* 뒤로 가기 로직 */
                                navController.popBackStack()
                            },
                            shape = RoundedCornerShape(28),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("수정 완료", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "프로필 사진 설정",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // 권한 상태 확인
                            when (ContextCompat.checkSelfPermission(context, permissionToRequest)) {
                                PackageManager.PERMISSION_GRANTED -> {
                                    // 권한이 이미 있으면 갤러리 실행
                                    galleryLauncher.launch("image/*")
                                }

                                else -> {
                                    // 권한이 없으면 권한 요청 팝업 띄우기
                                    permissionLauncher.launch(permissionToRequest)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "앨범에서 사진 선택",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedImageUri = null // 미리보기 이미지 제거
                            isProfileImageDeleted = true // 삭제되었음을 상태로 기록
                            showBottomSheet = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "프로필 사진 삭제",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}
