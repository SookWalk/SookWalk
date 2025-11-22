private val functions = Firebase.functions

fun sendOtp(email: String, onResult: (Boolean) -> Unit) {
    functions
        .getHttpsCallable("sendOtp")
        .call(mapOf("email" to email))
        .addOnSuccessListener {
            onResult(true)
        }
        .addOnFailureListener {
            onResult(false)
        }
}
