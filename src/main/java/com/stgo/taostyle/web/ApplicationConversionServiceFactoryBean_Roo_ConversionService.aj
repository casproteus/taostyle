// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.stgo.taostyle.web;

import com.stgo.taostyle.domain.Cart;
import com.stgo.taostyle.domain.City;
import com.stgo.taostyle.domain.Country;
import com.stgo.taostyle.domain.Customize;
import com.stgo.taostyle.domain.Person;
import com.stgo.taostyle.domain.Product;
import com.stgo.taostyle.domain.Service;
import com.stgo.taostyle.domain.TextContent;
import com.stgo.taostyle.domain.UserAccount;
import com.stgo.taostyle.web.ApplicationConversionServiceFactoryBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

privileged aspect ApplicationConversionServiceFactoryBean_Roo_ConversionService {
    
    declare @type: ApplicationConversionServiceFactoryBean: @Configurable;
    
    public Converter<Cart, String> ApplicationConversionServiceFactoryBean.getCartToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.Cart, java.lang.String>() {
            public String convert(Cart cart) {
                return new StringBuilder().append(cart.getQuantity()).append(' ').append(cart.getWeight()).append(' ').append(cart.getUnit()).append(' ').append(cart.getCreatedDate()).toString();
            }
        };
    }
    
    public Converter<Long, Cart> ApplicationConversionServiceFactoryBean.getIdToCartConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.Cart>() {
            public com.stgo.taostyle.domain.Cart convert(java.lang.Long id) {
                return Cart.findCart(id);
            }
        };
    }
    
    public Converter<String, Cart> ApplicationConversionServiceFactoryBean.getStringToCartConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.Cart>() {
            public com.stgo.taostyle.domain.Cart convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Cart.class);
            }
        };
    }
    
    public Converter<City, String> ApplicationConversionServiceFactoryBean.getCityToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.City, java.lang.String>() {
            public String convert(City city) {
                return new StringBuilder().append(city.getName()).toString();
            }
        };
    }
    
    public Converter<Long, City> ApplicationConversionServiceFactoryBean.getIdToCityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.City>() {
            public com.stgo.taostyle.domain.City convert(java.lang.Long id) {
                return City.findCity(id);
            }
        };
    }
    
    public Converter<String, City> ApplicationConversionServiceFactoryBean.getStringToCityConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.City>() {
            public com.stgo.taostyle.domain.City convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), City.class);
            }
        };
    }
    
    public Converter<Long, Country> ApplicationConversionServiceFactoryBean.getIdToCountryConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.Country>() {
            public com.stgo.taostyle.domain.Country convert(java.lang.Long id) {
                return Country.findCountry(id);
            }
        };
    }
    
    public Converter<String, Country> ApplicationConversionServiceFactoryBean.getStringToCountryConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.Country>() {
            public com.stgo.taostyle.domain.Country convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Country.class);
            }
        };
    }
    
    public Converter<Customize, String> ApplicationConversionServiceFactoryBean.getCustomizeToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.Customize, java.lang.String>() {
            public String convert(Customize customize) {
                return new StringBuilder().append(customize.getCusKey()).append(' ').append(customize.getCusValue()).toString();
            }
        };
    }
    
    public Converter<Long, Customize> ApplicationConversionServiceFactoryBean.getIdToCustomizeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.Customize>() {
            public com.stgo.taostyle.domain.Customize convert(java.lang.Long id) {
                return Customize.findCustomize(id);
            }
        };
    }
    
    public Converter<String, Customize> ApplicationConversionServiceFactoryBean.getStringToCustomizeConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.Customize>() {
            public com.stgo.taostyle.domain.Customize convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Customize.class);
            }
        };
    }
    
    public Converter<Person, String> ApplicationConversionServiceFactoryBean.getPersonToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.Person, java.lang.String>() {
            public String convert(Person person) {
                return new StringBuilder().append(person.getName()).toString();
            }
        };
    }
    
    public Converter<Long, Person> ApplicationConversionServiceFactoryBean.getIdToPersonConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.Person>() {
            public com.stgo.taostyle.domain.Person convert(java.lang.Long id) {
                return Person.findPerson(id);
            }
        };
    }
    
    public Converter<String, Person> ApplicationConversionServiceFactoryBean.getStringToPersonConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.Person>() {
            public com.stgo.taostyle.domain.Person convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Person.class);
            }
        };
    }
    
    public Converter<Product, String> ApplicationConversionServiceFactoryBean.getProductToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.Product, java.lang.String>() {
            public String convert(Product product) {
                return new StringBuilder().append(product.getPartNo()).append(' ').append(product.getName()).append(' ').append(product.getProducedplace()).append(' ').append(product.getC1()).toString();
            }
        };
    }
    
    public Converter<Long, Product> ApplicationConversionServiceFactoryBean.getIdToProductConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.Product>() {
            public com.stgo.taostyle.domain.Product convert(java.lang.Long id) {
                return Product.findProduct(id);
            }
        };
    }
    
    public Converter<String, Product> ApplicationConversionServiceFactoryBean.getStringToProductConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.Product>() {
            public com.stgo.taostyle.domain.Product convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Product.class);
            }
        };
    }
    
    public Converter<Service, String> ApplicationConversionServiceFactoryBean.getServiceToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.Service, java.lang.String>() {
            public String convert(Service service) {
                return new StringBuilder().append(service.getName()).append(' ').append(service.getDescription()).append(' ').append(service.getC1()).append(' ').append(service.getC2()).toString();
            }
        };
    }
    
    public Converter<Long, Service> ApplicationConversionServiceFactoryBean.getIdToServiceConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.Service>() {
            public com.stgo.taostyle.domain.Service convert(java.lang.Long id) {
                return Service.findService(id);
            }
        };
    }
    
    public Converter<String, Service> ApplicationConversionServiceFactoryBean.getStringToServiceConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.Service>() {
            public com.stgo.taostyle.domain.Service convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Service.class);
            }
        };
    }
    
    public Converter<TextContent, String> ApplicationConversionServiceFactoryBean.getTextContentToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.TextContent, java.lang.String>() {
            public String convert(TextContent textContent) {
                return new StringBuilder().append(textContent.getPosInPage()).append(' ').append(textContent.getContent()).toString();
            }
        };
    }
    
    public Converter<Long, TextContent> ApplicationConversionServiceFactoryBean.getIdToTextContentConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.TextContent>() {
            public com.stgo.taostyle.domain.TextContent convert(java.lang.Long id) {
                return TextContent.findTextContent(id);
            }
        };
    }
    
    public Converter<String, TextContent> ApplicationConversionServiceFactoryBean.getStringToTextContentConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.TextContent>() {
            public com.stgo.taostyle.domain.TextContent convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), TextContent.class);
            }
        };
    }
    
    public Converter<UserAccount, String> ApplicationConversionServiceFactoryBean.getUserAccountToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.stgo.taostyle.domain.UserAccount, java.lang.String>() {
            public String convert(UserAccount userAccount) {
                return new StringBuilder().append(userAccount.getLoginname()).append(' ').append(userAccount.getPassword()).append(' ').append(userAccount.getCel()).append(' ').append(userAccount.getTel()).toString();
            }
        };
    }
    
    public Converter<Long, UserAccount> ApplicationConversionServiceFactoryBean.getIdToUserAccountConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.stgo.taostyle.domain.UserAccount>() {
            public com.stgo.taostyle.domain.UserAccount convert(java.lang.Long id) {
                return UserAccount.findUserAccount(id);
            }
        };
    }
    
    public Converter<String, UserAccount> ApplicationConversionServiceFactoryBean.getStringToUserAccountConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.stgo.taostyle.domain.UserAccount>() {
            public com.stgo.taostyle.domain.UserAccount convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserAccount.class);
            }
        };
    }
    
    public void ApplicationConversionServiceFactoryBean.installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getCartToStringConverter());
        registry.addConverter(getIdToCartConverter());
        registry.addConverter(getStringToCartConverter());
        registry.addConverter(getCityToStringConverter());
        registry.addConverter(getIdToCityConverter());
        registry.addConverter(getStringToCityConverter());
        registry.addConverter(getCountryToStringConverter());
        registry.addConverter(getIdToCountryConverter());
        registry.addConverter(getStringToCountryConverter());
        registry.addConverter(getCustomizeToStringConverter());
        registry.addConverter(getIdToCustomizeConverter());
        registry.addConverter(getStringToCustomizeConverter());
        registry.addConverter(getPersonToStringConverter());
        registry.addConverter(getIdToPersonConverter());
        registry.addConverter(getStringToPersonConverter());
        registry.addConverter(getProductToStringConverter());
        registry.addConverter(getIdToProductConverter());
        registry.addConverter(getStringToProductConverter());
        registry.addConverter(getServiceToStringConverter());
        registry.addConverter(getIdToServiceConverter());
        registry.addConverter(getStringToServiceConverter());
        registry.addConverter(getTextContentToStringConverter());
        registry.addConverter(getIdToTextContentConverter());
        registry.addConverter(getStringToTextContentConverter());
        registry.addConverter(getUserAccountToStringConverter());
        registry.addConverter(getIdToUserAccountConverter());
        registry.addConverter(getStringToUserAccountConverter());
    }
    
    public void ApplicationConversionServiceFactoryBean.afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
}
