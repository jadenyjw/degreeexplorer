import React, { Component } from 'react';
import { AppRegistry, Text } from 'react-native';

//Import all screens.
import LoginScreen from "./src/screens/LoginScreen"
import CurrentStatusScreen from "./src/screens/CurrentStatusScreen"
import AcademicHistoryScreen from "./src/screens/AcademicHistoryScreen"
import PlannerScreen from "./src/screens/PlannerScreen"

export default class DegreeExplorerApp extends Component{

  constructor(props) {
    super(props);
    // Set not logged in as default state.
    this.state = {
      loggedIn: false
    }
  }


  render() {
    // Check if we're logged in and return a corresponding screen.
    if (this.state.loggedIn){
      return <CurrentStatusScreen></CurrentStatusScreen>
    }
    else{
      return <LoginScreen></LoginScreen>
    }
  }


}


AppRegistry.registerComponent('DegreeExplorerApp', () => DegreeExplorerApp);
