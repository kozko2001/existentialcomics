import React from 'react';

export function renderDevTools(store) {
  if (__DEV__) {
    let {DevTools, DebugPanel, LogMonitor} = require('redux-devtools/lib/react');
    return (
      <DebugPanel top right bottom
		  toggleVisibilityKey='ctrl-h'
		  changePositionKey='ctrl-q'>
	<DevTools store={store} monitor={LogMonitor} />
      </DebugPanel>
    );
  }

  return null;
}
