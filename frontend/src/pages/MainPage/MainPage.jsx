import { useState, useEffect } from 'react';
import style from './mainpage.module.css';

export const MainPage = () => {
    const [isTryToShort, setIsTryToShort] = useState(false);
    const [originalUrl, setOriginalUrl] = useState('');
    const [maxClicks, setMaxClicks] = useState('');
    const [expirationTime, setExpirationTime] = useState('');
    const [shortUrl, setShortUrl] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const id = localStorage.getItem('uuid');
    const [links, setLinks] = useState([]);

    useEffect(() => {
        const intervalId = setInterval(() => {
            if (id) {
                fetchLinks();
            }
        }, 1000);

        return () => clearInterval(intervalId);
    }, [id]);

    const fetchLinks = async () => {
        try {
            const response = await fetch('http://localhost:8000/links', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ uuid: id })
            });

            if (response.ok) {
                const data = await response.json();
                setLinks(data.links);
                console.log(data.links)
            } else {
                console.error('Error fetching links');
            }
        } catch (error) {
            console.error('Error fetching links:', error);
        }
    };


    const handleDeleteLink = async (linkId) => {
        try {
            const response = await fetch(`http://localhost:8000/links/delete/${linkId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                setLinks((prevLinks) => prevLinks.filter((link) => link.id !== linkId));
                console.log(`Link with ID ${linkId} deleted successfully`);
            } else {
                console.error('Error deleting link');
            }
        } catch (error) {
            console.error('Error deleting link:', error);
        }
    };

    const handleShortenLink = async (e) => {
        e.preventDefault();

        if (!originalUrl) {
            setErrorMessage('Введите корректную ссылку.');
            return;
        }

        setErrorMessage('');
        setShortUrl('');

        const payload = {
            body: {
                uuid: id,
                url: originalUrl,
                maxClicks: maxClicks || null,
                expirationTime: expirationTime || null
            }
        };

        try {
            const response = await fetch('http://localhost:8000/shorten', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                const data = await response.json();
                setShortUrl(data.shortUrl);
                fetchLinks()
            } else {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Ошибка создания короткой ссылки.');
            }
        } catch (error) {
            setErrorMessage('Ошибка соединения с сервером.');
        }
    };

    const handleCancel = () => {
        setIsTryToShort(false);
        setOriginalUrl('');
        setMaxClicks('');
        setExpirationTime('');
        setShortUrl('');
        setErrorMessage('');
    };

    return (
        <div className={style.mainPage}>
            <div className={style.left}>
                <div className={style.authHelper}>
                    <font className={style.descr}>Ваш UUID:</font>
                    <font className={style.uuid}>{id}</font>
                </div>
                <div className={style.linkFuncs}>
                    {isTryToShort ? (
                        <div onClick={handleCancel} className={style.makeLink}>
                            Отмена
                        </div>
                    ) : (
                        <div onClick={() => setIsTryToShort(true)} className={style.makeLink}>
                            Создать короткую ссылку
                        </div>
                    )}
                </div>
                {isTryToShort && (
                    <div className={style.linkform}>
                        <form className={style.form} onSubmit={handleShortenLink}>
                            <div className={style.formEl}>
                                <label htmlFor="originalUrl">Ссылка*</label>
                                <input
                                    type="text"
                                    id="originalUrl"
                                    value={originalUrl}
                                    onChange={(e) => setOriginalUrl(e.target.value)}
                                    placeholder="Введите ссылку"
                                    required
                                />
                            </div>
                            <div className={style.formEl}>
                                <label htmlFor="maxClicks">Макс. число переходов</label>
                                <input
                                    type="number"
                                    id="maxClicks"
                                    value={maxClicks}
                                    onChange={(e) => setMaxClicks(e.target.value)}
                                    placeholder="Пример: 10"
                                />
                            </div>
                            <div className={style.formEl}>
                                <label htmlFor="expirationTime">Время жизни (в секундах)</label>
                                <input
                                    type="number"
                                    id="expirationTime"
                                    value={expirationTime}
                                    onChange={(e) => setExpirationTime(e.target.value)}
                                    placeholder="Пример: 3600 (1 час)"
                                />
                            </div>
                            <div className={style.formActions}>
                                <button type="submit" className={style.submitButton}>
                                    Создать
                                </button>
                            </div>
                        </form>
                        {shortUrl && (
                            <div className={style.result}>
                                <p>Короткая ссылка создана:</p>
                                <a href={shortUrl} target="_blank" rel="noopener noreferrer">
                                    {shortUrl}
                                </a>
                            </div>
                        )}
                        {errorMessage && <p className={style.errorMessage}>{errorMessage}</p>}
                    </div>
                )}
            </div>
            <div className={style.left}>
            <h2>Ваши ссылки:</h2>
                <div className={style.linksList}>
                    {links.map((link) => (
                        <div key={link.id} className={style.linkItem}>
                            <p>Оригинал: {link.originalUrl}</p>
                            <p>
                                Короткая: <a href={'http://localhost:8000/' + link.shortUrl} target="_blank">{'http://localhost:8000/' + link.shortUrl}</a>
                            </p>
                            <p>Переходов: {link.clicks}/{link.maxClicks || '∞'}</p>
                            <p>Время жизни: {Math.max(link.expirationTime - (Date.now() / 1000 - link.createdAt), 0)} сек.</p>
                            {link.isActive 
                                ? <p>Ссылка работает</p>
                                : <p style={{color: 'red'}}>Ссылка недействительна</p>
                            }
                            <button onClick={() => handleDeleteLink(link.id)} className={style.linkDelete}>Удалить ссылку</button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};