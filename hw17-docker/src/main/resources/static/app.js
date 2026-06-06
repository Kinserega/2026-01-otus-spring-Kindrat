const app = (function() {
    'use strict';

    const API = {
        BOOKS: '/api/books',
        AUTHORS: '/api/authors',
        GENRES: '/api/genres',
        COMMENTS: '/api/comments'
    };

    let currentBooks = [];
    let currentAuthors = [];
    let currentGenres = [];

    function init() {
        loadBooks();
        loadAuthors();
        loadGenres();
        setupEventListeners();
    }

    function setupEventListeners() {
        document.getElementById('book-form').addEventListener('submit', handleFormSubmit);
    }

    async function loadBooks() {
        try {
            const response = await fetch(API.BOOKS);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            currentBooks = await response.json();
            renderBooks(currentBooks);
        } catch (error) {
            console.error('Error loading books:', error);
            showMessage('Ошибка загрузки книг: ' + error.message, 'error');
            document.getElementById('books-content').innerHTML =
                '<p class="error-message">Не удалось загрузить книги</p>';
        }
    }

    async function loadAuthors() {
        try {
            const response = await fetch(API.AUTHORS);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            currentAuthors = await response.json();
            populateAuthorSelect();
        } catch (error) {
            console.error('Error loading authors:', error);
        }
    }

    async function loadGenres() {
        try {
            const response = await fetch(API.GENRES);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            currentGenres = await response.json();
            populateGenreSelect();
        } catch (error) {
            console.error('Error loading genres:', error);
        }
    }

    function populateAuthorSelect() {
        const select = document.getElementById('book-author');
        select.innerHTML = '<option value="">-- Выберите автора --</option>';

        currentAuthors.forEach(author => {
            const option = document.createElement('option');
            option.value = author.id;
            option.textContent = author.fullName || author.name;
            select.appendChild(option);
        });
    }

    function populateGenreSelect(selectedIds = []) {
        const select = document.getElementById('book-genres');
        select.innerHTML = '';

        currentGenres.forEach(genre => {
            const option = document.createElement('option');
            option.value = genre.id;
            option.textContent = genre.name;
            if (selectedIds.includes(genre.id)) {
                option.selected = true;
            }
            select.appendChild(option);
        });
    }

    function renderBooks(books) {
        const container = document.getElementById('books-content');

        if (books.length === 0) {
            container.innerHTML = '<p>Книг пока нет. Добавьте первую книгу!</p>';
            return;
        }

        const table = document.createElement('table');
        table.className = 'books';

        table.innerHTML = `
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Название</th>
                    <th>Автор</th>
                    <th>Жанры</th>
                    <th>Действия</th>
                </tr>
            </thead>
            <tbody>
                ${books.map(book => `
                    <tr data-book-id="${book.id}">
                        <td>${book.id}</td>
                        <td>${escapeHtml(book.title)}</td>
                        <td>${escapeHtml(book.authorName || book.author?.fullName || 'N/A')}</td>
                        <td>
                            ${(book.genreNames || book.genres || []).map(genre =>
                                `<span class="genre-tag">${escapeHtml(typeof genre === 'string' ? genre : genre.name)}</span>`
                            ).join('')}
                        </td>
                        <td>
                            <button class="btn btn-edit" onclick="app.editBook(${book.id})">Редактировать</button>
                            <button class="btn btn-delete" onclick="app.deleteBook(${book.id})">Удалить</button>
                            <button class="btn btn-comments" onclick="app.showComments(${book.id})">Комментарии</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        `;

        container.innerHTML = '';
        container.appendChild(table);
    }

    function renderAuthors(authors) {
        const container = document.getElementById('authors-content');

        if (authors.length === 0) {
            container.innerHTML = '<p>Авторов пока нет</p>';
            return;
        }

        const table = document.createElement('table');
        table.className = 'authors';

        table.innerHTML = `
            <thead>
                <tr>
                    <th>ID</th>
                    <th>ФИО</th>
                </tr>
            </thead>
            <tbody>
                ${authors.map(author => `
                    <tr>
                        <td>${author.id}</td>
                        <td>${escapeHtml(author.fullName || author.name)}</td>
                    </tr>
                `).join('')}
            </tbody>
        `;

        container.innerHTML = '';
        container.appendChild(table);
    }

    function renderGenres(genres) {
        const container = document.getElementById('genres-content');

        if (genres.length === 0) {
            container.innerHTML = '<p>Жанров пока нет</p>';
            return;
        }

        const table = document.createElement('table');
        table.className = 'genres';

        table.innerHTML = `
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Название</th>
                </tr>
            </thead>
            <tbody>
                ${genres.map(genre => `
                    <tr>
                        <td>${genre.id}</td>
                        <td>${escapeHtml(genre.name)}</td>
                    </tr>
                `).join('')}
            </tbody>
        `;

        container.innerHTML = '';
        container.appendChild(table);
    }

    async function loadComments(bookId) {
        try {
            const response = await fetch(`${API.COMMENTS}/${bookId}/comments`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error loading comments:', error);
            showMessage('Ошибка загрузки комментариев: ' + error.message, 'error');
            return [];
        }
    }

    async function handleFormSubmit(event) {
        event.preventDefault();

        const bookId = document.getElementById('book-id').value;
        const title = document.getElementById('book-title').value.trim();
        const authorId = document.getElementById('book-author').value;
        const genreSelect = document.getElementById('book-genres');
        const genreIds = Array.from(genreSelect.selectedOptions).map(opt => parseInt(opt.value));

        clearErrors();
        let hasErrors = false;

        if (!title) {
            showError('title-errors', 'Название обязательно');
            hasErrors = true;
        }

        if (!authorId) {
            showError('author-errors', 'Автор обязателен');
            hasErrors = true;
        }

        if (genreIds.length === 0) {
            showError('genre-errors', 'Выберите хотя бы один жанр');
            hasErrors = true;
        }

        if (hasErrors) return;

        const bookData = {
            title: title,
            authorId: parseInt(authorId),
            genreIds: genreIds
        };

        try {
            let response;

            if (bookId) {
                // Обновление существующей книги
                response = await fetch(`${API.BOOKS}/${bookId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(bookData)
                });
            } else {
                // Создание новой книги
                response = await fetch(API.BOOKS, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(bookData)
                });
            }

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            showMessage(bookId ? 'Книга обновлена' : 'Книга добавлена', 'success');
            closeModal();
            await loadBooks();
        } catch (error) {
            console.error('Error saving book:', error);
            showMessage('Ошибка сохранения: ' + error.message, 'error');
        }
    }

    async function editBook(bookId) {
        const book = currentBooks.find(b => b.id === bookId);
        if (!book) return;

        document.getElementById('modal-title').textContent = 'Редактировать книгу';
        document.getElementById('book-id').value = book.id;
        document.getElementById('book-title').value = book.title;
        document.getElementById('book-author').value = book.authorId || book.author?.id;

        const genreIds = book.genreIds || (book.genres || []).map(g => g.id);
        populateGenreSelect(genreIds);

        document.getElementById('book-modal').style.display = 'block';
    }

    async function deleteBook(bookId) {
        if (!confirm('Вы уверены, что хотите удалить эту книгу?')) return;

        try {
            const response = await fetch(`${API.BOOKS}/${bookId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            showMessage('Книга удалена', 'success');
            await loadBooks();
        } catch (error) {
            console.error('Error deleting book:', error);
            showMessage('Ошибка удаления: ' + error.message, 'error');
        }
    }

    async function showComments(bookId) {
        const book = currentBooks.find(b => b.id === bookId);
        if (!book) return;

        document.getElementById('comments-book-title').textContent =
            `Комментарии к книге: ${book.title}`;

        const commentsContent = document.getElementById('comments-content');
        commentsContent.innerHTML = '<p class="loading">Загрузка...</p>';
        document.getElementById('comments-modal').style.display = 'block';

        const comments = await loadComments(bookId);
        renderComments(comments);
    }

    function renderComments(comments) {
        const container = document.getElementById('commentsList');

        if (!comments || comments.length === 0) {
            container.innerHTML = '<p>Комментариев пока нет</p>';
            return;
        }

        container.innerHTML = comments.map(comment => `
            <div class="comment">
                <div class="comment-text">${escapeHtml(comment.text)}</div>
            </div>
        `).join('');
    }

    function showBooks() {
        document.getElementById('books-section').style.display = 'block';
        document.getElementById('authors-section').style.display = 'none';
        document.getElementById('genres-section').style.display = 'none';
    }

    function showAuthors() {
        document.getElementById('books-section').style.display = 'none';
        document.getElementById('authors-section').style.display = 'block';
        document.getElementById('genres-section').style.display = 'none';
        renderAuthors(currentAuthors);
    }

    function showGenres() {
        document.getElementById('books-section').style.display = 'none';
        document.getElementById('authors-section').style.display = 'none';
        document.getElementById('genres-section').style.display = 'block';
        renderGenres(currentGenres);
    }

    function openModal() {
        document.getElementById('modal-title').textContent = 'Добавить книгу';
        document.getElementById('book-form').reset();
        document.getElementById('book-id').value = '';
        populateGenreSelect([]);
        document.getElementById('book-modal').style.display = 'block';
    }

    function closeModal() {
        document.getElementById('book-modal').style.display = 'none';
        clearErrors();
    }

    function closeCommentsModal() {
        document.getElementById('comments-modal').style.display = 'none';
    }

    function showMessage(text, type) {
        const container = document.getElementById('message-container');
        const message = document.createElement('div');
        message.className = type === 'error' ? 'error-message' : 'success-message';
        message.textContent = text;

        container.innerHTML = '';
        container.appendChild(message);

        setTimeout(() => {
            message.remove();
        }, 3000);
    }

    function showError(elementId, message) {
        document.getElementById(elementId).textContent = message;
    }

    function clearErrors() {
        document.querySelectorAll('.errors').forEach(el => el.textContent = '');
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    window.onclick = function(event) {
        const bookModal = document.getElementById('book-modal');
        const commentsModal = document.getElementById('comments-modal');

        if (event.target === bookModal) {
            closeModal();
        }
        if (event.target === commentsModal) {
            closeCommentsModal();
        }
    };

    return {
        init,
        showBooks,
        showAuthors,
        showGenres,
        openModal,
        closeModal,
        closeCommentsModal,
        editBook,
        deleteBook,
        showComments
    };
})();

document.addEventListener('DOMContentLoaded', app.init);