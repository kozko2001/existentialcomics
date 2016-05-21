const tests = require.context('.', true, /.+\.test\.jsx?$/);
tests.keys().forEach(tests);
