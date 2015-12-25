//
//  ViewController.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/9/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import UIKit
import SwiftyJSON

class ViewController: UIViewController {

  override func viewDidLoad() {
    super.viewDidLoad()
    let testJsonStr = "{\"id\": 1,\"name\": \"A green door\",\"price\": 12.50,\"tags\": [\"home\", \"green\"]}".dataUsingEncoding(NSUTF8StringEncoding)
    var testJson = JSON(data:testJsonStr!)
    print(testJson)
    print(testJson["id"])
    print(testJson["name"].stringValue)

    // Do any additional setup after loading the view, typically from a nib.
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }


}

