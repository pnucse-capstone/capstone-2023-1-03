import './App.css';
import {Route, Router, Routes} from "react-router-dom";
import Main from "./pages/Main";
import CardiacViewer from "./pages/CardiacViewer";
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

const darkTheme = createTheme({
    palette: {
        mode: 'dark',
    },
});

function App() {
  return (
      <ThemeProvider theme={darkTheme}>
          <CssBaseline />
          <Routes>
              <Route path={"/"} element={<Main />}></Route>
              <Route path={"/viewer/:patientNumber"} element={<CardiacViewer />}></Route>
          </Routes>
      </ThemeProvider>
  );
}

export default App;
