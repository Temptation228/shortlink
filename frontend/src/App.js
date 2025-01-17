import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthPage } from './pages/AuthPage/AuthPage';
import { MainPage } from './pages/MainPage/MainPage';
import PrivateRoute from './services/PrivateRoute';
import { Header } from './components/Header/Header';
import { Footer } from './components/Footer/Footer';

function App() {
  return (
    <div className="App">
      <Router>
        <Header />
        <div className="content"> 
          <Routes>
            <Route path='/' element={<AuthPage />} />
            <Route path='/main' element={
                <PrivateRoute>
                    <MainPage />
                </PrivateRoute>
            }/>
          </Routes>
        </div>
        <Footer />
      </Router>
    </div>
  );
}

export default App;
