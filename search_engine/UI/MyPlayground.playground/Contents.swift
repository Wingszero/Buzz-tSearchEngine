//: Playground - noun: a place where people can play

import UIKit

func checkIfWeatherQuery(rawQuery: String)->String?{
  let query = rawQuery.lowercaseString
  if !query.containsString("weather"){
    return nil
  }
  let tokens = query.componentsSeparatedByString(" ")
  var cityName = ""
  for t in tokens {
    if t != "in" && t != "weather" {
      cityName += t
    }
  }
  return cityName.characters.count > 0 ? cityName : "Philadelphia"
}

checkIfWeatherQuery("weather in budapest")
checkIfWeatherQuery("weather london")
checkIfWeatherQuery("bryn mawr weather")



