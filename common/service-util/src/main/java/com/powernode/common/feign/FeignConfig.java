package com.powernode.common.feign;

import com.powernode.common.FeignCustomerDataDecoder;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Decoder decoder(ObjectFactory<HttpMessageConverters> objectFactory, ObjectProvider<HttpMessageConverterCustomizer> customizers){
        return new FeignCustomerDataDecoder(new SpringDecoder(objectFactory, customizers));
    }
}
