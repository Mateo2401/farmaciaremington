document.addEventListener('DOMContentLoaded', function () {
    const yearEl = document.getElementById('currentYear');
    if (yearEl) {
        yearEl.textContent = new Date().getFullYear();
    }

    const sidebarLinks = document.querySelectorAll('.nav-link');
    sidebarLinks.forEach((link) => {
        link.addEventListener('click', () => {
            sidebarLinks.forEach((item) => item.classList.remove('active'));
            link.classList.add('active');
        });
    });

    const chatForm = document.getElementById('aiChatForm');
    if (!chatForm) {
        return;
    }

    const chatPanel = document.getElementById('farmacia-ai');
    const chatLauncher = document.getElementById('aiChatLauncher');
    const chatClose = document.getElementById('aiChatClose');
    const chatNavButton = document.getElementById('aiChatNavButton');
    const questionInput = document.getElementById('aiQuestion');
    const messages = document.getElementById('aiChatMessages');
    const status = document.getElementById('aiChatStatus');
    const questionCount = document.getElementById('aiQuestionCount');
    const sendButton = document.getElementById('aiSendButton');
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    function setChatOpen(open) {
        chatPanel.hidden = !open;
        chatLauncher.setAttribute('aria-expanded', String(open));
        if (open) {
            questionInput.focus();
        } else {
            chatLauncher.focus();
        }
    }

    chatLauncher.addEventListener('click', () => {
        setChatOpen(chatPanel.hidden);
    });
    chatClose.addEventListener('click', () => setChatOpen(false));
    chatNavButton.addEventListener('click', () => setChatOpen(true));
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && !chatPanel.hidden) {
            setChatOpen(false);
        }
    });

    function addMessage(text, role) {
        const message = document.createElement('div');
        message.className = `ai-message ai-message-${role}`;

        const label = document.createElement('span');
        label.className = 'ai-message-label';
        label.textContent = role === 'user' ? 'Tú' : 'Farmacia AI';

        const content = document.createElement('p');
        content.textContent = text;

        message.append(label, content);
        messages.appendChild(message);
        messages.scrollTop = messages.scrollHeight;
    }

    questionInput.addEventListener('input', () => {
        questionCount.textContent = `${questionInput.value.length} / 1000`;
    });

    chatForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const pregunta = questionInput.value.trim();
        if (!pregunta) {
            status.textContent = 'Escribe una pregunta antes de enviar.';
            questionInput.focus();
            return;
        }

        addMessage(pregunta, 'user');
        questionInput.value = '';
        questionCount.textContent = '0 / 1000';
        questionInput.disabled = true;
        sendButton.disabled = true;
        status.textContent = 'Farmacia AI está preparando una respuesta...';

        try {
            const response = await fetch('/api/farmacia-ai/chat', {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({ pregunta })
            });

            if (response.redirected && new URL(response.url).pathname === '/login') {
                window.location.assign(response.url);
                return;
            }

            const result = await response.json();
            if (!response.ok) {
                throw new Error(result.error || 'No se pudo completar la consulta.');
            }
            if (typeof result.respuesta !== 'string' || !result.respuesta.trim()) {
                throw new Error('El asistente devolvió una respuesta vacía.');
            }

            addMessage(result.respuesta, 'assistant');
            status.textContent = 'Respuesta recibida.';
        } catch (error) {
            status.textContent = error.message || 'No fue posible conectar con Farmacia AI.';
        } finally {
            questionInput.disabled = false;
            sendButton.disabled = false;
            if (!chatPanel.hidden) {
                questionInput.focus();
            }
        }
    });
});
