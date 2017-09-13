package com.stgo.taostyle.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

import com.stgo.taostyle.domain.Country;

/**
 * A central place to register application converters and formatters.
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    public Converter<Country, String> getCountryToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.Country, java.lang.String>() {
            public String convert(
                    Country country) {
                if (country.getDescription() != null && !country.getDescription().isEmpty()) {
                    return new StringBuilder().append(country.getName()).append(" - ").append(country.getDescription())
                            .toString();
                } else {
                    return new StringBuilder().append(country.getName()).toString();
                }
            }
        };
    }
    
    public static void main(String[] args) {

    	byte[] GS_ExclamationMark = new byte[] {0x1d, 0x21, 0x11 };
    	
    	String c = "29, 33, 17";
    	String[] pieces = c.split(",");
    	if(pieces.length != 3) {
    		return;
    	}
    	
    	for(int i = 0; i < 3; i++) {
	    	GS_ExclamationMark[i] = Integer.valueOf(pieces[i].trim()).byteValue();
    	}
    	
    	String a = new String(GS_ExclamationMark);
    	byte[] b = a.getBytes();
    	if(b.length != 3 || b[0] != 0x1d || b[1] != 0x21 || b[2] != 0x11) {
    		System.out.println("fail");
    	}
    }
}
