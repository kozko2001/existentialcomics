'use strict';


var path = require('path');
var webpackConfig = require('./webpack.config.js');

const testsPath = path.resolve(__dirname, './test/index.js');

var karmaConfig = {
  files: [
    {pattern: testsPath, watched: false, included: true, served: true}
  ],
  frameworks: [
    'mocha',
    'chai',
  ],
  preprocessors: {
    [testsPath]: ['webpack', 'sourcemap']
  },
  reporters: ['mocha'],
  browsers: ['Chrome'],
  webpack: webpackConfig,
  plugins: [
    'karma-webpack',
    'karma-sourcemap-loader',
    'karma-mocha',
    'karma-mocha-reporter',
    'karma-chai',
    'karma-chrome-launcher',
    'karma-sinon'
  ],
  singleRun: false,
  autoWatch: true,
  autoWatchBatchDelay: 0,
  reportSlowerThan: 2000,
  concurrency: 2,
  browserNoActivityTimeout: 300000,
};

module.exports = function(config) {
  config.set(karmaConfig)
}
