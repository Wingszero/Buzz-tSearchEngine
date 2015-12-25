//
//  SearchResultTableViewController.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/9/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import UIKit
import SwiftyJSON

class SearchResultTableViewController: UITableViewController {
  let weatherManager = WeatherManager.sharedWeatherManager
  let searchManager = SearchManager.sharedSearchManager
  var previousQuery = ""
  // MARK: - Weather UI.
  
  @IBOutlet weak var weather_icon: UIImageView!
  @IBOutlet weak var temperature: UILabel!
  @IBOutlet weak var current_temperature: UILabel!
  @IBOutlet weak var change_temp_button: UIButton!
  @IBOutlet weak var weather_description: UILabel!
  @IBOutlet weak var weather_background: UIImageView!
  @IBOutlet weak var weather_updated_time: UILabel!
  
  @IBAction func handleTempUnitChange(sender: AnyObject) {
    weatherManager.toggleWeatherUnit()
    if let weather = currentWeatherCache {
      updateView(withNewWeather: weather)
    }
  }
  
  var currentWeatherCache:WeatherForLocation?
  
  override func viewDidLoad() {
    super.viewDidLoad()
    weather_view.hidden = true
    // Uncomment the following line to preserve selection between presentations
    // self.clearsSelectionOnViewWillAppear = false
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem()
  }
  
  @IBOutlet weak var weather_view: UIView!
  func updateSearch(forQuery query:String) {
    if query == previousQuery {
      return
    }
    // Else new query. Process it.
    previousQuery = query
    if let location = SearchManager.sharedSearchManager.checkIfWeatherQuery(query) {
      weather_view.hidden = false
      getAndUpdateWeather(forLocation: location)
    } else {
      weather_view.hidden = true
    }
    searchManager.newSearch(query, tableView: self.tableView)
  }
  
  func getAndUpdateWeather(forLocation location: String) {
    print("retriving weather at \(location)")
    if let url = weatherManager.configureUrl(forLocation: location){
      let dataTask = weatherManager.urlSession.dataTaskWithURL(url, completionHandler: {data, response, error
        in
        if let d = data {
          var weatherJson = JSON(data: d)
          if weatherJson["cod"].stringValue != "200" {
            print("Error" + weatherJson["cod"].stringValue + weatherJson["message"].stringValue + self.weatherManager.currentLocationAndWeather.locationString)
          } else {
            print("got new weather")
            let newWeather = self.weatherManager.updateCurrentWeatherModel(weatherJson)
            self.currentWeatherCache = newWeather
            self.updateView(withNewWeather: newWeather)
          }
        } else {
          print(error)
        }
      })
      dataTask.resume()
    }
  }
  
  func updateView(withNewWeather weather: WeatherForLocation){
    print("updating weather view")
    weather_updated_time.text = "Updated on: " + weatherManager.dateAndTimeToShortString(NSDate())
    
    if weatherManager.currentUnit == .Celc {
      current_temperature.text = weather.curTempInCel()
      temperature.text = weather.minTempInCel() + " / " + weather.maxTempInCel()
      change_temp_button.setTitle("C", forState: UIControlState.Normal)
    } else {
      current_temperature.text = weather.curTempInFahr()
      temperature.text = weather.minTempInFahr() + " / " + weather.maxTempInFahr()
      change_temp_button.setTitle("F", forState: UIControlState.Normal)
    }
    
    weather_description.text = weather.description
    weather_icon.image = UIImage(named: weather.iconName)
    weather_background.image = UIImage(named: weatherManager.backgroundImageName(forIcon: weather.iconName))
  }
  
  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }
  
  // MARK: - Table view data source
  
  override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
    return 1
  }
  
  override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return SearchManager.sharedSearchManager.getNumberOfResultsReturned()
  }
  
  
  override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
    let cell = tableView.dequeueReusableCellWithIdentifier("SearchResultTableCell", forIndexPath: indexPath) as! SearchResultTableCell
    if let result = SearchManager.sharedSearchManager.getResult(atIdx: indexPath.row) {
      cell.title.text = result.title
      cell.url.text = result.url
      cell.abstract.text = result.abstract
    } else {
      cell.title.text = "Hello World " + String(indexPath.row)
      cell.url.text = "http://www.google.com"
      cell.abstract.text = String(indexPath.row) + " this is an abstract"
    }

    return cell
  }
  
  override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
    return 120.0
  }
  
  
  
  // MARK: - Navigation
  // In a storyboard-based application, you will often want to do a little preparation before navigation
  override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
    let destination = segue.destinationViewController as! WebViewController
    // get url data given indexPath
    let rowIdx = tableView.indexPathForSelectedRow!.row
    var url = "http://www.google.com"
    if let result = searchManager.getResult(atIdx: rowIdx) {
      url = result.url
    }
    destination.currentUrlStr = url
  }
  
}
