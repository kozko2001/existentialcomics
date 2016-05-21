import React from 'react';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import lightBaseTheme from 'material-ui/styles/baseThemes/lightBaseTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';


const lightMuiTheme = getMuiTheme(lightBaseTheme);

export default class extends React.Component {
  constructor(props) {
    super(props);
  }

 
  render() {
    return (
      <div>
	<MuiThemeProvider muiTheme={lightMuiTheme}>

	  {this.props.children}
	</MuiThemeProvider>
      </div>
    );
  }
};
