package com.esh7enly.esh7enlyuser.activity

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object StringSer: Serializer<UserTest> {

    override val defaultValue: UserTest
        get() = UserTest()

    override suspend fun readFrom(input: InputStream): UserTest {
        TODO("Not yet implemented")
    }

    override suspend fun writeTo(t: UserTest, output: OutputStream) {
        TODO("Not yet implemented")
    }
}