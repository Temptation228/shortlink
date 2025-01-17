import style from './authpage.module.css';
import React, { useState } from 'react';

export const AuthPage = () => {
    const [uuid, setUuid] = useState('');
    const [password, setPassword] = useState('');
    const [isRegistering, setIsRegistering] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(''); 

        if (isRegistering && !password) {
            setError("Необходим пароль для регистрации");
            return;
        }

        if (!isRegistering && !uuid) {
            setError("UUID необходим для входа");
            return;
        }

        const requestBody = {
            body: isRegistering
                ? { password }
                : { uuid, password },
        };

        const endpoint = isRegistering ? 'http://localhost:8000/register' : 'http://localhost:8000/auth';

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody),
            });
            console.log('Отправленный запрос:', JSON.stringify(requestBody));
            const data = await response.json();

            if (!response.ok) {
                setError(data.message || 'Ошибка при обработке ответа.');
                return;
            }

            if (isRegistering) {
                localStorage.setItem('uuid', data.uuid);
                window.location.href = '/main';
            } else {
                localStorage.setItem('uuid', uuid); 
                window.location.href = '/main'; 
            }
        } catch (error) {
            console.error('Error:', error);
            setError('Ошибка подключения, попробуйте еще раз.');
        }
    };

    return (
        <div className={style.authPage}>
            <font className={style.head}>{isRegistering ? 'Регистрация' : 'Вход'}</font>
            <form className={style.form} onSubmit={handleSubmit}>
                {!isRegistering && 
                    <div className={style.formEl}>
                        <label>UUID</label>
                        <input 
                            type="text" 
                            value={uuid} 
                            onChange={(e) => setUuid(e.target.value)} 
                            required
                            placeholder='Введите UUID'
                        />
                    </div>
                }
                <div className={style.formEl}>
                    <label>Password</label>
                    <input 
                        type="password" 
                        value={password} 
                        onChange={(e) => setPassword(e.target.value)} 
                        required
                        placeholder='Введите пароль'
                    />
                </div>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <div className={style.buttonContainer}>
                    <button className={style.goToBtn} type="submit">{isRegistering ? 'Регистрация' : 'Вход'}</button>
                    <button 
                        className={style.changeBtn}
                        type="button" 
                        onClick={() => setIsRegistering(!isRegistering)}
                    >
                        {isRegistering ? 'Уже есть аккаунт? Войти' : 'Нет аккаунта? Зарегистрироваться'}
                    </button>
                </div>
            </form>
        </div>
    );
};