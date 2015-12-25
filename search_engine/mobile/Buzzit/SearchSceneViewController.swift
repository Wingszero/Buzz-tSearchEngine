//
//  SearchSceneViewController.swift
//  Buzzit
//
//  Created by Yunqi Chen on 12/9/15.
//  Copyright © 2015 Angie Yunqi Chen. All rights reserved.
//

import UIKit

class SearchSceneViewController: UIViewController {
  var searchResultVC: SearchResultTableViewController?
  
  @IBOutlet weak var buzzit_large_logo: UIImageView!
  @IBOutlet weak var search_box_to_top_constraint: NSLayoutConstraint!
  @IBOutlet weak var search_input_view: UIView!
  
  override func viewDidLoad() {
    super.viewDidLoad()
    searchResultVC = self.childViewControllers[0] as? SearchResultTableViewController
  }
  
  override func viewWillAppear(animated: Bool) {
    super.viewWillAppear(animated)
    search_input_view.layer.shadowOpacity = 1
    search_input_view.layer.shadowRadius = 9 
    search_input_view.layer.shadowColor = UIColor.blackColor().CGColor
  }
  
  @IBAction func handleReturnHomeButtonTap(sender: AnyObject) {
    UIView.animateWithDuration(0.6, animations: { _ in
      self.buzzit_large_logo.hidden = false
      self.search_box_to_top_constraint.constant = 160
      self.view.layoutIfNeeded()
      }, completion: nil)
  }
  
  @IBAction func handleQueryValueChanged(sender: AnyObject) {
    moveTextBoxToTop()
  }
  
  @IBAction func handleEditingDidBegin(sender: AnyObject) {
    moveTextBoxToTop()
  }
  
  @IBOutlet weak var query_input_field: UITextField!
  @IBAction func handleQueryBoxDidEndOnExit(sender: AnyObject) {
    moveTextBoxToTop()
    if query_input_field.text!.characters.count > 0 {
      searchResultVC?.updateSearch(forQuery: query_input_field.text!)
    }
    view.endEditing(true)
  }
  
  func moveTextBoxToTop() {
    if self.search_box_to_top_constraint.constant == 0 {
      return
    }
    buzzit_large_logo.hidden = true
    UIView.animateWithDuration(0.6, delay: 0, usingSpringWithDamping: 0.60, initialSpringVelocity: 2, options: .CurveEaseInOut,animations: { _ in
      self.search_box_to_top_constraint.constant = 0 // Original constant = 100
      self.view.layoutIfNeeded()
      }, completion: nil)
  }
  
  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }
  
  
  /*
  // MARK: - Navigation
  
  // In a storyboard-based application, you will often want to do a little preparation before navigation
  override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
  // Get the new view controller using segue.destinationViewController.
  // Pass the selected object to the new view controller.
  }
  */
  
}
