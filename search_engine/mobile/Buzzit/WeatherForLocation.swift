//
//  WeatherForLocation.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/9/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import Foundation

class WeatherForLocation{
  //location info
  var locationString:String!
  var useLocationString = true
  
  var cityName: String!
  var lat = Double(0)
  var long =  Double(0)
  
  //weather info
  var iconName:String!
  var description:String!
  var temperature_K = Double(0)
  var maxTemperature_K = Double(0)
  var minTemperature_K = Double(0)
  
  init(name: String){
    self.locationString = name
  }
  
  init(lat: Double, long: Double){
    self.lat = lat
    self.long = long
  }
  
  
  //MARK: - get temperatures -- syntax sugar
  func maxTempInCel()->String!{
    return String(stringInterpolationSegment: kelvinToCelsius(maxTemperature_K))
  }
  
  func minTempInCel()->String{
    return String(kelvinToCelsius(minTemperature_K))
  }
  
  func curTempInCel()->String{
    return String(kelvinToCelsius(temperature_K))
  }
  
  func maxTempInFahr()->String{
    return String(kelvinToFahr(maxTemperature_K))
  }
  
  func minTempInFahr()->String{
    return String(kelvinToFahr(minTemperature_K))
  }
  
  func curTempInFahr()->String{
    return String(kelvinToFahr(temperature_K))
  }
  
  func kelvinToCelsius(tempInK: Double)->Int{
    return Int(round(tempInK - 273.15))
  }
  
  func kelvinToFahr(tempInK: Double)->Int{
    return Int(round((tempInK - 273.15) * 1.8000 + 32.0))
  }
}