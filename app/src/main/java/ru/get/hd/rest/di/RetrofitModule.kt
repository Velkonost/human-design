package ru.get.hd.rest.di

import android.content.Context
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import retrofit2.Converter
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.get.hd.BuildConfig
import ru.get.hd.di.scope.AppScope
import ru.get.hd.rest.RestService

@Module(includes = [(JsonModule::class)])
class RetrofitModule(val context: Context) {

//    @AppScope
//    @Provides
//    fun retrofitService(): RestService =
//        createRetrofit(BuildConfig.REST_BASE_URL, context).create(RestService::class.java)

    @AppScope
    @Provides
    fun restService(): RestService =
        createRetrofit(BuildConfig.BASE_URL, context).create(RestService::class.java)
}

@Module
class JsonModule {

    @Provides
    @AppScope
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        return objectMapper
    }

    @Provides
    @AppScope
    fun converterFactory(mapper: ObjectMapper): Converter.Factory =
        JacksonConverterFactory.create(mapper)
}
