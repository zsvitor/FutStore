document.addEventListener('DOMContentLoaded', function () {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });
    const thumbnails = document.querySelectorAll('.thumbnail-preview');
    thumbnails.forEach(thumb => {
        thumb.addEventListener('click', function () {
            const targetIndex = this.getAttribute('data-bs-slide-to');
            const carousel = document.getElementById('productCarousel');
            const bsCarousel = bootstrap.Carousel.getInstance(carousel);
            bsCarousel.to(parseInt(targetIndex));
            thumbnails.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
        });
    });
});
function incrementQuantity() {
    const input = document.getElementById('quantidade');
    const maxValue = parseInt(input.getAttribute('max') || 999);
    const currentValue = parseInt(input.value) || 1;
    if (currentValue < maxValue) {
        input.value = currentValue + 1;
    }
}
function decrementQuantity() {
    const input = document.getElementById('quantidade');
    const currentValue = parseInt(input.value) || 1;
    if (currentValue > 1) {
        input.value = currentValue - 1;
    }
}
document.addEventListener('DOMContentLoaded', function () {
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) {
        btnLogout.addEventListener('click', function (e) {
            e.preventDefault();
            const logoutModal = new bootstrap.Modal(document.getElementById('logoutModal'));
            logoutModal.show();
        });
    }
});