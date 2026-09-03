const DETALL_SUBLIM_MIN_LOADING_TIME = 2000;

const loadingStartedAt = Date.now();

let appIsReady = false;
let minimumTimePassed = false;

function hideDetallSublimLoader() {
  if (!appIsReady || !minimumTimePassed) {
    return;
  }

  const loader = document.getElementById('ds-app-loader');

  if (!loader) {
    return;
  }

  loader.classList.add('ds-app-loading--hidden');

  setTimeout(() => {
    document.documentElement.classList.add('ds-app-loaded');
  }, 180);

  setTimeout(() => {
    loader.remove();
  }, 600);
}

window.addEventListener(
  'detall-sublim-app-ready',
  function () {
    appIsReady = true;

    hideDetallSublimLoader();
  },
  { once: true },
);

const elapsedTime = Date.now() - loadingStartedAt;

const remainingTime = Math.max(0, DETALL_SUBLIM_MIN_LOADING_TIME - elapsedTime);

setTimeout(function () {
  minimumTimePassed = true;

  hideDetallSublimLoader();
}, remainingTime);

window.onload = function () {
  setTimeout(showError, 10000);
};

function showError() {
  if (appIsReady) {
    return;
  }

  const loader = document.getElementById('ds-app-loader');

  const errorElm = document.getElementById('jhipster-error');

  if (loader) {
    loader.classList.add('ds-app-loading--hidden');

    setTimeout(() => {
      loader.remove();
    }, 600);
  }

  if (errorElm?.style) {
    errorElm.style.display = 'block';
  }
}
