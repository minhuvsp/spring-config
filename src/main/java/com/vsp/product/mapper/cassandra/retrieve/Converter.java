package com.vsp.product.mapper.cassandra.retrieve;


public abstract class Converter<From, To> {

     public abstract void convert(From from, To to);


}
