package com.example.go_fit.api

class MemberApi {
    companion object{
        val BASE_URL = "https://gofit123.xyz/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "member"
        val GET_BY_USERNAME = BASE_URL + "member/"
        val ADD_URL = BASE_URL + "member"
        val UPDATE_URL = BASE_URL + "member/"
        val DELETE_URL = BASE_URL + "member/"
    }
}