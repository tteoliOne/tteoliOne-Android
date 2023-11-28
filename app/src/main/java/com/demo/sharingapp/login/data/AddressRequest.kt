package com.demo.sharingapp.login.data

data class AddressRequest(
    val results: Results
)

data class Results(
    val common: Common,
    val juso : List<Juso>
)

data class Common(
    val errorMessage : String,
    val countPerPage : String
)

data class Juso(
    val roadAddrPart1 : String,
    val jibunAddr : String
)
