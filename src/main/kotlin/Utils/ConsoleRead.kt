package com.mediacleaner.Utils

object ConsoleRead {
    fun getString(msg: String, default: String): String {
        print("$msg [$default]: ")
        val input = readLine()
        if(input == "")
            return default
        return input!!
    }

    fun getInt(msg: String, default: Int, acceptableInts: List<Int> = emptyList(), acceptableErrorMsg: String = "Something went wrong!"): Int {
        print("$msg [$default]: ")
        val input = readLine()
        if(input == "")
            return default

        var inputInt = input!!.toIntOrNull()

        if(inputInt == null) {
            println("Your input is not valid! Give me a number!")
            inputInt = getInt(msg, default)
        }

        if(!acceptableInts.isEmpty())
        {
            if(acceptableInts.contains(inputInt))
                return inputInt
            println(acceptableErrorMsg)
            inputInt = getInt(msg, default, acceptableInts, acceptableErrorMsg)
        }

        return inputInt
    }

    fun getBoolean(msg: String, default: Boolean, acceptableAsTrueAndFalse: List<String> = listOf("true", "false")): Boolean {
        val default2 = if(default)
            acceptableAsTrueAndFalse[0]
        else
            acceptableAsTrueAndFalse[1]

        print("$msg (${acceptableAsTrueAndFalse[0]}|${acceptableAsTrueAndFalse[1]}) [$default2]: ")
        val input = readLine()
        if(input == "")
            return default

        return when (input) {
            acceptableAsTrueAndFalse[0] -> true
            acceptableAsTrueAndFalse[1] -> false
            else -> {
                if(acceptableAsTrueAndFalse[0] == "true" && acceptableAsTrueAndFalse[1] == "false")
                    println("Your input is not valid! Give me the word true or false.!")
                getBoolean(msg, default, acceptableAsTrueAndFalse)
            }
        }
    }
}