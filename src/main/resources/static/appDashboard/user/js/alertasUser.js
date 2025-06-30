document.addEventListener('DOMContentLoaded', function() {
    const alerta = document.getElementById('alerta-flotante');
    if (alerta) {
        requestAnimationFrame(() => {
            alerta.style.opacity = '1';
            alerta.style.transform = 'translateY(0)';
        });
        setTimeout(() => {
            alerta.style.opacity = '0';
            alerta.style.transform = 'translateY(-20px)';
        }, 5000);
    }
});
