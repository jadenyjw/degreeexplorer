import React, { Component } from 'react';
import {
    ScrollView,
    Text,
    TextInput,
    View,
    Button
} from 'react-native';

export default class LoginScreen extends Component {

  _login = (username, password) => {
    const myRequest = new Request('https://acorn.utoronto.ca')
    fetch(myRequest)
    .then(response => {
      if (response.status === 200) {
        console.log(response);

      }
    })
    .then(response => {
      console.debug(response);
      // ...
    }).catch(error => {
      console.log(error);
    });
  }

  constructor(props){
    super(props);

  }

  render() {

    console.log("Login results:" + this._login("wangy841", "WordPass@123"));
    return (
      <Text>_login</Text>

    );

  }
}
