/**
 *  Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 *
 *  Licensed under the Apache License, Version 2.0 (the 'License');
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an 'AS IS' BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import '@talend/bootstrap-theme/src/theme/theme.scss';

// code rendering
import 'react-ace';
import 'brace/theme/chrome';
import 'brace/ext/language_tools';
import 'brace/mode/java';
import 'brace/mode/python';
import 'brace/mode/sql';
import 'brace/snippets/java';
import 'brace/snippets/sql';
import 'brace/snippets/python';

import store from './store';

import App from './components/App';

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('component-kit-tools-webapp')
);
