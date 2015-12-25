//
//  WeatherManager.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/9/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import Foundation
import SwiftyJSON

class WeatherManager{
  enum TempUnit {
    case Fahr
    case Celc
  }
  
  let dateFormatter = NSDateFormatter()
  var urlSession: NSURLSession!
  var myAPIKey = "&APPID=efd8b9328f60222aa40ccfe867fce9af"
  var currentLocationAndWeather:WeatherForLocation
  var currentUnit = TempUnit.Celc
  
  class var sharedWeatherManager: WeatherManager {
    struct Singleton {
      static let instance = WeatherManager()
    }
    return Singleton.instance
  }
  
  init(){
    currentLocationAndWeather = WeatherForLocation(name: "Philadelphia")
    initUrlSession()
  }
  
  func toggleWeatherUnit() {
    if currentUnit == .Fahr {
      currentUnit = .Celc
    } else {
      currentUnit = .Fahr
    }
  }
  
  func updateCurrentWeatherModel(json: JSON)->WeatherForLocation {
    currentLocationAndWeather.description = json["weather"][0]["description"].stringValue
    currentLocationAndWeather.iconName = json["weather"][0]["icon"].stringValue
    
    currentLocationAndWeather.cityName = json["name"].stringValue
    currentLocationAndWeather.lat = json["coord"]["lat"].doubleValue
    currentLocationAndWeather.long = json["coord"]["lon"].doubleValue
    
    currentLocationAndWeather.temperature_K = json["main"]["temp"].doubleValue
    currentLocationAndWeather.maxTemperature_K = json["main"]["temp_max"].doubleValue
    currentLocationAndWeather.minTemperature_K = json["main"]["temp_min"].doubleValue
    
    return currentLocationAndWeather
  }

  //MARK: - URL processing
  func initUrlSession(){
    let sessionConfig = NSURLSessionConfiguration.defaultSessionConfiguration()
    urlSession = NSURLSession(configuration: sessionConfig, delegate:nil, delegateQueue: NSOperationQueue.mainQueue())
  }
  
  
  func configureUrlForSixteenDaysForecast()->NSURL!{
    var urlStr = "http://api.openweathermap.org/data/2.5/forecast/daily?" //q=budapest&cnt=16&mode=json
    
    if currentLocationAndWeather.useLocationString {
      urlStr += configureUrlSubstring(currentLocationAndWeather.locationString)
    } else {
      urlStr += configureUrlSubstring(currentLocationAndWeather.lat, lon: currentLocationAndWeather.long)
    }
    urlStr += "&cnt=16&mode=json"
    return NSURL(string:urlStr)
  }
  
  func configureUrlForCurrentWeather()->NSURL!{
    let strPrefix = "http://api.openweathermap.org/data/2.5/weather?"
    var urlStr = ""
    if currentLocationAndWeather.useLocationString {
      urlStr = strPrefix + configureUrlSubstring(currentLocationAndWeather.locationString)
    } else {
      urlStr = strPrefix + configureUrlSubstring(currentLocationAndWeather.lat, lon: currentLocationAndWeather.long)
    }
    return NSURL(string: urlStr)
  }
  
  func configureUrl(forLocation location: String)->NSURL!{
    let strPrefix = "http://api.openweathermap.org/data/2.5/weather?"
      return NSURL(string: strPrefix + configureUrlSubstring(location))
  }

  
  func configureUrlSubstring(location: String) -> String{
    let locStrFiltered = preprocessLocationString(location)
    let urlString =  "q=\(locStrFiltered)" + myAPIKey
    return urlString
  }
  
  func preprocessLocationString(location: String)->String{
    return location.stringByReplacingOccurrencesOfString(" ", withString: ",", options: NSStringCompareOptions.LiteralSearch, range: nil)
  }
  
  func configureUrlSubstring(lat:Double, lon:Double) -> String{
    let urlString =  "lat=\(lat)&lon=\(lon)" + myAPIKey
    return urlString
  }
  
  //MARK: - current weather
  func curTemp()->String{
    if currentUnit == .Celc {
      return currentLocationAndWeather.curTempInCel()
    } else {
      return currentLocationAndWeather.curTempInFahr()
    }
  }
  
  func curMaxTemp()->String{
    if currentUnit == .Celc {
      return currentLocationAndWeather.maxTempInCel()
    } else {
      return currentLocationAndWeather.maxTempInFahr()
    }
  }
  
  func curMinTemp()->String{
    if currentUnit == .Celc {
      return currentLocationAndWeather.minTempInCel()
    } else {
      return currentLocationAndWeather.minTempInFahr()
    }
  }
  
  //MARK: - forecasting
  func maxTempIn16Days(rawJson:JSON)->[Int]{
    return tempIn16DaysHelper(rawJson, key: "max")
  }
  
  func minTempIn16Days(rawJson:JSON)->[Int]{
    return tempIn16DaysHelper(rawJson, key: "min")
  }
  
  func dayTempIn16Days(rawJson:JSON)->[Int]{
    return tempIn16DaysHelper(rawJson, key: "day")
  }
  
  
  private func tempIn16DaysHelper(rawJson:JSON, key:String)->[Int]{
    var temp = [Int]()
    var json = rawJson["list"]
    for idx in 0..<json.count {
      let rawTemp = json[idx]["temp"][key].doubleValue
      if currentUnit == .Celc {
        temp.append(kelvinToCelsius(rawTemp))
      } else {
        temp.append(kelvinToFahr(rawTemp))
      }
    }
    print(temp)
    return temp
  }
  
  func kelvinToCelsius(tempInK: Double)->Int{
    return Int(round(tempInK - 273.15))
  }
  
  func kelvinToFahr(tempInK: Double)->Int{
    return Int(round((tempInK - 273.15) * 1.8000 + 32.0))
  }
  
  func dateAndTimeToShortString(date: NSDate)->String{
    dateFormatter.dateStyle = NSDateFormatterStyle.ShortStyle
    dateFormatter.timeStyle = NSDateFormatterStyle.ShortStyle
    
    return dateFormatter.stringFromDate(date)
  }
  
  func backgroundImageName(forIcon icon:String)->String{
    if icon.characters.last == "n" {
      return "night_bg"
    }
    if icon == "09d" || icon == "10d" || icon == "11d" {
      return "rainy_bg"
    }
    if icon == "12d" {
      return "snowy_bg"
    }
    return "sunny_bg"
  }
}
