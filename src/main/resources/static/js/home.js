const swiper = new Swiper('.heroSwiper', {
  loop: true,
  effect: 'fade',
  fadeEffect: { crossFade: true },
  speed: 850,
  autoplay: { delay: 5000, disableOnInteraction: false, pauseOnMouseEnter: true },
  navigation: { nextEl: '.swiper-button-next', prevEl: '.swiper-button-prev' },
  pagination: { el: '.swiper-pagination', clickable: true }
});
document.querySelectorAll('.qs-tab').forEach(btn => btn.addEventListener('click', () => {
  document.querySelectorAll('.qs-tab').forEach(x => x.classList.remove('active'));
  document.querySelectorAll('.qs-panel').forEach(x => x.classList.remove('active'));
  btn.classList.add('active'); document.querySelector(`[data-panel="${btn.dataset.target}"]`).classList.add('active');
}));
