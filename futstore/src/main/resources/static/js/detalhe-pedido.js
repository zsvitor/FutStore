document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('btnLogout')?.addEventListener('click', function (e) {
        e.preventDefault();
        var logoutModal = new bootstrap.Modal(document.getElementById('logoutModal'));
        logoutModal.show();
    });
});