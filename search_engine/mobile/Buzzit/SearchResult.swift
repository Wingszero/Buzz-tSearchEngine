//
//  SearchResult.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/15/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import Foundation

class SearchResult {
  var title:String
  var url:String
  var abstract:String
  
  init(title:String, url:String, abstract:String) {
    self.title = title
    self.url = url
    self.abstract = abstract
  }
}