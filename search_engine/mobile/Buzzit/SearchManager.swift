//
//  SearchManager.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/10/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import Foundation
import SwiftyJSON

class SearchManager {
  class var sharedSearchManager: SearchManager {
    struct Singleton {
      static let instance = SearchManager()
    }
    return Singleton.instance
  }

  var currentResults:JSON?
  var urlSession:NSURLSession
  
  let rawUrl = "http://ec2-54-174-6-163.compute-1.amazonaws.com:8081/search_result"
  
  /**
  * JSON sample (validated)
  * activated when ?RETURN_JSON=yes
  * {
  * "time-spent": 10,
  *  "total-num-of-results": 20,
  * "num-of-results-returned": 2,
  *  "results": [
  *    {
  *    "title": "this is the title",
  *    "url": "https://www.foo.bar",
  *    "content": "lorem ipsum"
  *    }, {
  *    "title": "this is the title - No.2",
  *    "url": "https://www.foo.bar2",
  *    "content": "lorem ipsum 2"
  *    }
  *  ]
  *}
  */
  init () {
    // TODO: remove initial dummy data
    let rawStr = "{\"time-spent\":3, \"total-num-of-results\":20,\"num-of-results-returned\":3,\"results\":[{\"title\":\"Check out Google instead\", \"url\":\"http://www.google.com\", \"content\": \"Google.com\"},{\"title\":\"Check out Bing\", \"url\":\"http://www.bing.com\", \"content\": \"Bing.com\"},{\"title\":\"Check out Bing\", \"url\":\"http://www.bing.com\", \"content\": \"Bing.com\"}]}"
    let data: NSData = rawStr.dataUsingEncoding(NSUTF8StringEncoding)!
    currentResults = JSON(data: data)
    let sessionConfig = NSURLSessionConfiguration.defaultSessionConfiguration()
    urlSession = NSURLSession(configuration: sessionConfig, delegate:nil, delegateQueue: NSOperationQueue.mainQueue())
  }
  
  func newSearch(query: String, tableView:UITableView) {
    currentResults = nil;
    tableView.reloadData()
    if let url = NSURL(string: self.rawUrl+"?RETURN_JSON=yes"){
      let request = NSMutableURLRequest(URL: url)
      request.HTTPMethod = "POST"
      let paramString = "SEARCH_KEY=\(query)"
      request.HTTPBody = paramString.dataUsingEncoding(NSUTF8StringEncoding)
      
      print("new search:")
      print(request)
      let dataTask = urlSession.dataTaskWithRequest(request, completionHandler: {data, response, error
        in
        if let d = data {
          print("data:")
          print(NSString(data: d, encoding: NSUTF8StringEncoding))
          self.currentResults = JSON(data:d)
          print("current Results: ")
          print(self.currentResults)
          tableView.reloadData()
        } else {
          self.currentResults = nil
          print("error:")
          print(error)
          tableView.reloadData()
        }
      })
      dataTask.resume()
    }
  }
  
  func getResult(atIdx idx:Int)->SearchResult? {
    if let json = currentResults {
      var result = json["results"][idx]
      print(result)
      return SearchResult(title: result["title"].stringValue, url: result["url"].stringValue, abstract: result["content"].stringValue)
    }
    return nil
  }
  
  func getNumberOfResultsReturned()->Int{
    if let json = currentResults {
      return json["num-of-results-returned"].intValue
    }
    return 0
  }
  
  
  
  
  // Check if the query is asking for weather.
  // Return nil if not; otherwise return the name of the city to check.
  // Sample acceptable weather queries:
  // - weather
  // - weather in <city>
  // - weather <city>
  // - <city> weather
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
}



