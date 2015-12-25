//
//  WebViewController.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/10/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import UIKit

class WebViewController: UIViewController {

  @IBOutlet weak var web_view: UIWebView!
  var currentUrlStr = "http://www.google.com"
  
  
  override func viewDidLoad() {
    super.viewDidLoad()
    loadUrl()
  }
  
  @IBAction func handleReturnButtonTap2(sender: AnyObject) {
    self.dismissViewControllerAnimated(true, completion: nil)
  }
  
  func loadUrl() {
    print(currentUrlStr)
    if let url = NSURL(string: currentUrlStr) {
      let request = NSURLRequest(URL: url)
      web_view.loadRequest(request)
    }
  }
}
