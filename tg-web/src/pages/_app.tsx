import store from '@/app/stores/store';
import '@/app/styles/globals.css';
import React from 'react';
import { Toaster } from 'react-hot-toast';
import { Provider } from 'react-redux';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const App = ({ Component, pageProps }: { Component: any; pageProps: any }) => {
  return (
    <React.StrictMode>
      <Provider store={store}>
        <Component {...pageProps} />
      </Provider>
      <Toaster />
    </React.StrictMode>
  );
};

export default App;
