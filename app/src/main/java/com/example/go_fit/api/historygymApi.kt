package com.example.go_fit.api

class historygymApi {
    companion object{
        val BASE_URL = "http://192.168.1.5/Server_Go_Fit/public/"

        val GET_ALL_URL = BASE_URL + "presensigym"
        val GET_BY_USERNAME = BASE_URL + "presensigym/"
        val ADD_URL = BASE_URL + "presensigym"
        val UPDATE_URL = BASE_URL + "presensigym/"
        val DELETE_URL = BASE_URL + "presensigym/"
    }
}