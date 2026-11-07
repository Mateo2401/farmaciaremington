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
});
