const RR = (() => {
  const tokenKey = 'rapidrent.token';
  const userKey = 'rapidrent.user';

  function token() { return localStorage.getItem(tokenKey); }
  function user() {
    try { return JSON.parse(localStorage.getItem(userKey) || 'null'); }
    catch (_) { return null; }
  }
  function setSession(jwt, currentUser) {
    localStorage.setItem(tokenKey, jwt);
    localStorage.setItem(userKey, JSON.stringify(currentUser));
  }
  function clearSession() {
    localStorage.removeItem(tokenKey);
    localStorage.removeItem(userKey);
  }
  function headers(json = true) {
    const h = {};
    if (json) h['Content-Type'] = 'application/json';
    if (token()) h.Authorization = `Bearer ${token()}`;
    return h;
  }
  async function api(url, options = {}) {
    const response = await fetch(url, options);
    const text = await response.text();
    let data = text;
    try { data = text ? JSON.parse(text) : null; } catch (_) {}
    if (!response.ok) {
      throw new Error(typeof data === 'string' ? data : (data?.message || 'A apărut o eroare.'));
    }
    return data;
  }
  function alertBox(id, message, ok = true) {
    const el = document.getElementById(id);
    if (!el) return;
    el.className = `alert show ${ok ? 'alert-ok' : 'alert-bad'}`;
    el.textContent = message;
  }
  function money(value) { return `${Number(value || 0).toFixed(0)} RON`; }
  function roleLabel(role) {
    return String(role || '').replace('ROLE_', '').toLowerCase();
  }
  function dashboardFor(currentUser) {
    if (!currentUser) return '/ui/login';
    if (currentUser.role === 'ROLE_ADMIN') return '/ui/admin';
    if (currentUser.role === 'ROLE_FURNIZOR') return '/ui/provider';
    return '/ui/client';
  }
  async function refreshMe() {
    const current = await api('/api/auth/me', { headers: headers(false) });
    localStorage.setItem(userKey, JSON.stringify(current));
    updateNav();
    return current;
  }
  function updateNav() {
    const u = user();
    document.querySelectorAll('[data-auth-label]').forEach(el => {
      el.textContent = u ? `Salut, ${u.username}` : 'Autentificare';
      el.href = u ? dashboardFor(u) : '/ui/login';
    });
    document.querySelectorAll('[data-logout]').forEach(el => {
      el.classList.toggle('hidden', !u);
      el.addEventListener('click', (event) => {
        event.preventDefault();
        clearSession();
        window.location.href = '/ui';
      });
    });
    document.querySelectorAll('[data-role]').forEach(el => {
      const role = el.getAttribute('data-role');
      el.classList.toggle('hidden', !u || u.role !== role);
    });
  }
  function requireAuth(...roles) {
    const u = user();
    if (!token() || !u) {
      window.location.href = '/ui/login';
      return null;
    }
    if (roles.length && !roles.includes(u.role)) {
      window.location.href = dashboardFor(u);
      return null;
    }
    return u;
  }
  function readSearchParams() {
    const params = new URLSearchParams(window.location.search);
    return {
      keyword: params.get('keyword') || params.get('location') || '', // Păstrăm location ca un fallback de siguranță
      startDate: params.get('startDate') || '',
      endDate: params.get('endDate') || '',
      category: params.get('category') || '',
      transmission: params.get('transmission') || '',
      maxPrice: params.get('maxPrice') || ''
    };
  }
  function syncSearchForm(form) {
    const params = readSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      const field = form?.querySelector(`[name="${key}"]`);
      if (field) field.value = value;
    });
  }
  function carVisual(car) {
    if (car.imageUrl) return `<img src="${escapeHtml(car.imageUrl)}" alt="${escapeHtml(car.brand)}" style="width:100%;height:100%;object-fit:cover">`;
    return '🚗';
  }
  function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>'"]/g, ch => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[ch]));
  }
  function carCard(car, dates = {}) {
    return `<article class="car-card">
      <div class="car-visual">${carVisual(car)}</div>
      <div class="car-body">
        <div class="car-title">${escapeHtml(car.brand)} ${escapeHtml(car.model)}</div>
        <div class="muted">${escapeHtml(car.location || 'București')} · ${escapeHtml(car.licensePlate || 'Număr disponibil după rezervare')}</div>
        <div class="pills">
          <span class="pill">${escapeHtml(car.category || 'Compact')}</span>
          <span class="pill">${escapeHtml(car.transmission || 'Manuală')}</span>
          <span class="pill">${escapeHtml(car.seats || 5)} locuri</span>
          <span class="pill">Asistență 24/7</span>
        </div>
        <p class="muted small">Preț transparent, fără costuri ascunse. Rezervarea este validată în funcție de disponibilitate și statusul actelor.</p>
      </div>
      <div class="car-price">
        <div>
          <div class="muted small">de la</div>
          <div class="price">${money(car.price)}</div>
          <div class="muted small">pe zi</div>
        </div>
        <button class="btn btn-primary" data-reserve data-car-id="${car.id}" data-start="${escapeHtml(dates.startDate || '')}" data-end="${escapeHtml(dates.endDate || '')}">Rezervă acum</button>
      </div>
    </article>`;
  }
  async function loadCars() {
    const list = document.getElementById('carsList');
    if (!list) return;
    const form = document.getElementById('carsSearchForm');
    syncSearchForm(form);
    const params = readSearchParams();
    const apiParams = new URLSearchParams();
    ['keyword', 'category', 'transmission', 'maxPrice'].forEach(key => {
      if (params[key]) apiParams.set(key, params[key]);
    });
    list.innerHTML = '<div class="card">Se încarcă mașinile disponibile...</div>';
    try {
      const cars = await api(`/api/client/cars${apiParams.toString() ? '?' + apiParams : ''}`);
      if (!cars.length) {
        list.innerHTML = '<div class="card"><h3>Nu am găsit mașini disponibile</h3><p class="muted">Încearcă să modifici locația, categoria sau bugetul maxim.</p></div>';
        return;
      }
      list.innerHTML = cars.map(car => carCard(car, params)).join('');
      list.querySelectorAll('[data-reserve]').forEach(btn => btn.addEventListener('click', reserveFromButton));
    } catch (err) {
      list.innerHTML = `<div class="card"><h3>Eroare</h3><p class="muted">${escapeHtml(err.message)}</p></div>`;
    }
  }
  async function reserveFromButton(event) {
    const u = requireAuth('ROLE_CLIENT');
    if (!u) return;
    const btn = event.currentTarget;
    let startDate = btn.dataset.start;
    let endDate = btn.dataset.end;
    if (!startDate || !endDate) {
      startDate = prompt('Data preluării (YYYY-MM-DD):', new Date().toISOString().slice(0,10));
      endDate = prompt('Data predării (YYYY-MM-DD):', new Date(Date.now()+86400000).toISOString().slice(0,10));
    }
    if (!startDate || !endDate) return;
    try {
      const message = await api('/api/client/reserve', {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify({ clientId: u.id, carId: Number(btn.dataset.carId), startDate, endDate })
      });
      sessionStorage.setItem('rapidrent.lastReservationMessage', message);
      window.location.href = '/ui/reservation-confirmation';
    } catch (err) {
      alert(err.message);
      if (err.message.toLowerCase().includes('actele')) window.location.href = '/ui/verification';
    }
  }
  return { api, headers, alertBox, user, token, setSession, clearSession, refreshMe, updateNav, requireAuth, dashboardFor, money, escapeHtml, loadCars, syncSearchForm, readSearchParams };
})();

document.addEventListener('DOMContentLoaded', () => {
  // --- Funcționalitate Modal GDPR ---
  const openGdprBtn = document.getElementById('openGdprModal');
  const closeGdprBtn = document.getElementById('closeGdprModal');
  const acceptGdprBtn = document.getElementById('acceptGdprModal');
  const gdprModal = document.getElementById('gdprModal');
  const gdprCheckbox = document.querySelector('input[name="gdprConsent"]');

  if (openGdprBtn && gdprModal) {
    // Deschide modalul la click pe link
    openGdprBtn.addEventListener('click', (e) => {
      e.preventDefault();
      gdprModal.classList.remove('hidden');
    });

    // Funcție pentru ascunderea modalului
    const closeModal = () => gdprModal.classList.add('hidden');

    // Închide la apăsarea lui X
    if (closeGdprBtn) closeGdprBtn.addEventListener('click', closeModal);

    // Butonul "Am înțeles" bifează automat căsuța și închide modalul
    if (acceptGdprBtn) {
      acceptGdprBtn.addEventListener('click', () => {
        closeModal();
        if (gdprCheckbox) gdprCheckbox.checked = true;
      });
    }

    // Închide modalul dacă utilizatorul dă click în afara cutiei albe (pe overlay-ul întunecat)
    gdprModal.addEventListener('click', (e) => {
      if (e.target === gdprModal) closeModal();
    });
  }
  RR.updateNav();

  const homeSearch = document.getElementById('homeSearchForm');
  if (homeSearch) {
    RR.syncSearchForm(homeSearch);
    homeSearch.addEventListener('submit', event => {
      event.preventDefault();
      const params = new URLSearchParams(new FormData(homeSearch));
      window.location.href = `/ui/cars?${params.toString()}`;
    });
  }

  const carsSearch = document.getElementById('carsSearchForm');
  if (carsSearch) {
    carsSearch.addEventListener('submit', event => {
      event.preventDefault();
      const params = new URLSearchParams(new FormData(carsSearch));
      window.location.href = `/ui/cars?${params.toString()}`;
    });
    RR.loadCars();
  }

  const loginForm = document.getElementById('loginForm');
  if (loginForm) loginForm.addEventListener('submit', async event => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(loginForm));
    try {
      const jwt = await RR.api('/api/auth/login', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload) });
      localStorage.setItem('rapidrent.token', jwt);
      const current = await RR.refreshMe();
      window.location.href = RR.dashboardFor(current);
    } catch (err) { RR.alertBox('authAlert', err.message, false); }
  });

  const registerForm = document.getElementById('registerForm');
  if (registerForm) registerForm.addEventListener('submit', async event => {
    event.preventDefault();
    const fd = new FormData(registerForm);
    const payload = {
      username: fd.get('username'), email: fd.get('email'), password: fd.get('password'),
      isProvider: fd.get('isProvider') === 'on', gdprConsent: fd.get('gdprConsent') === 'on'
    };
    try {
      const message = await RR.api('/api/auth/register', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload) });
      RR.alertBox('registerAlert', message + ' Te poți autentifica acum.', true);
      registerForm.reset();
    } catch (err) { RR.alertBox('registerAlert', err.message, false); }
  });

  const resetForm = document.getElementById('resetForm');
  if (resetForm) resetForm.addEventListener('submit', async event => {
    event.preventDefault();
    const email = new FormData(resetForm).get('email');
    try {
      const message = await RR.api(`/api/auth/reset-password?email=${encodeURIComponent(email)}`, { method: 'POST' });
      RR.alertBox('resetAlert', message, true);
    } catch (err) { RR.alertBox('resetAlert', err.message, false); }
  });

  initClientPage();
  initVerificationPage();
  initProviderPage();
  initProviderCarForm();
  initAdminPage();
  initConfirmationPage();
});

async function initClientPage() {
  const root = document.getElementById('clientPage');
  if (!root) return;
  const u = RR.requireAuth('ROLE_CLIENT');
  if (!u) return;
  document.getElementById('clientName').textContent = u.username;
  document.getElementById('documentStatus').textContent = u.documentStatus;
  try {
    const reservations = await RR.api(`/api/client/reservations/${u.id}`, { headers: RR.headers(false) });
    const tbody = document.getElementById('reservationRows');
    tbody.innerHTML = reservations.length ? reservations.map(r => `<tr>
      <td>#${r.id}</td><td>${RR.escapeHtml(r.carName)}</td><td>${RR.escapeHtml(r.location || '-')}</td>
      <td>${r.startDate} → ${r.endDate}</td><td>${RR.money(r.totalPrice)}</td><td>${RR.escapeHtml(r.status)}</td>
      <td>${r.status === 'ACTIVE' ? `<button class="btn btn-danger" data-cancel-reservation="${r.id}">Anulează</button>` : ''}</td>
    </tr>`).join('') : '<tr><td colspan="7" class="muted">Nu ai rezervări încă.</td></tr>';
    tbody.querySelectorAll('[data-cancel-reservation]').forEach(btn => btn.addEventListener('click', async () => {
      if (!confirm('Sigur vrei să anulezi această rezervare?')) return;
      try {
        const msg = await RR.api(`/api/client/cancel/${btn.dataset.cancelReservation}?clientId=${u.id}`, { method: 'POST', headers: RR.headers(false) });
        RR.alertBox('clientAlert', msg, true); setTimeout(() => location.reload(), 900);
      } catch (err) { RR.alertBox('clientAlert', err.message, false); }
    }));
  } catch (err) { RR.alertBox('clientAlert', err.message, false); }

  const changePasswordForm = document.getElementById('changePasswordForm');
  changePasswordForm?.addEventListener('submit', async event => {
    event.preventDefault();
    const fd = new FormData(changePasswordForm);
    const url = `/api/auth/change-password/${u.id}?oldPassword=${encodeURIComponent(fd.get('oldPassword'))}&newPassword=${encodeURIComponent(fd.get('newPassword'))}`;
    try {
      const msg = await RR.api(url, { method: 'POST', headers: RR.headers(false) });
      RR.alertBox('clientAlert', msg, true); changePasswordForm.reset();
    } catch (err) { RR.alertBox('clientAlert', err.message, false); }
  });
}

async function initVerificationPage() {
  const form = document.getElementById('verificationForm');
  if (!form) return;
  const u = RR.requireAuth('ROLE_CLIENT');
  if (!u) return;
  document.getElementById('verificationUser').textContent = `${u.username} · status: ${u.documentStatus}`;
  form.addEventListener('submit', async event => {
    event.preventDefault();
    try {
      const response = await fetch(`/api/client/documents/upload/${u.id}`, { method: 'POST', headers: { Authorization: `Bearer ${RR.token()}` }, body: new FormData(form) });
      const text = await response.text();
      if (!response.ok) throw new Error(text);
      RR.alertBox('verificationAlert', text, true); form.reset(); await RR.refreshMe();
    } catch (err) { RR.alertBox('verificationAlert', err.message, false); }
  });
}

async function initProviderPage() {
  const root = document.getElementById('providerPage');
  if (!root) return;
  const u = RR.requireAuth('ROLE_FURNIZOR');
  if (!u) return;
  document.getElementById('providerName').textContent = u.username;
  try {
    const dashboard = await RR.api(`/api/provider/cars/dashboard/${u.id}`, { headers: RR.headers(false) });
    document.getElementById('providerCarsCount').textContent = dashboard.totalCarsListed;
    document.getElementById('providerIncome').textContent = RR.money(dashboard.netIncome);
    
    const cars = await RR.api(`/api/provider/cars/provider/${u.id}`, { headers: RR.headers(false) });
    const tbody = document.getElementById('providerCarRows');
    
    tbody.innerHTML = cars.length ? cars.map(c => `<tr>
      <td>#${c.id}</td>
      <td>${RR.escapeHtml(c.brand)} ${RR.escapeHtml(c.model)}</td>
      <td>${RR.escapeHtml(c.location || '-')}</td>
      <td>${RR.money(c.price)}</td>
      <td>${RR.escapeHtml(c.status)}</td>
      <td>
        <button class="btn btn-danger" style="padding: 6px 12px; font-size: 13px;" data-delete-car="${c.id}">Șterge</button>
      </td>
    </tr>`).join('') : '<tr><td colspan="6" class="muted">Nu ai adăugat încă mașini.</td></tr>';

    tbody.querySelectorAll('[data-delete-car]').forEach(btn => btn.addEventListener('click', async () => {
      if (!confirm('Sigur vrei să ștergi definitiv această mașină din flotă?')) return;
      try {
        const msg = await RR.api(`/api/provider/cars/delete/${btn.dataset.deleteCar}`, { 
          method: 'DELETE', 
          headers: RR.headers(false) 
        });
        RR.alertBox('providerAlert', msg, true);
        setTimeout(() => location.reload(), 900);
      } catch (err) { 
        RR.alertBox('providerAlert', err.message, false); 
      }
    }));

  } catch (err) { RR.alertBox('providerAlert', err.message, false); }
}

function initProviderCarForm() {
  const form = document.getElementById('providerCarForm');
  if (!form) return;
  const u = RR.requireAuth('ROLE_FURNIZOR');
  if (!u) return;
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const fd = new FormData(form);
    const payload = Object.fromEntries(fd);
    payload.providerId = u.id;
    payload.price = Number(payload.price);
    payload.seats = Number(payload.seats || 5);
    try {
      const msg = await RR.api('/api/provider/cars/add', { method: 'POST', headers: RR.headers(), body: JSON.stringify(payload) });
      RR.alertBox('providerCarAlert', msg, true); form.reset();
    } catch (err) { RR.alertBox('providerCarAlert', err.message, false); }
  });
}

async function initAdminPage() {
  const root = document.getElementById('adminPage');
  if (!root) return;
  RR.requireAuth('ROLE_ADMIN');
  try {
    const dashboard = await RR.api('/api/admin/cars/dashboard', { headers: RR.headers(false) });
    document.getElementById('adminActiveCars').textContent = dashboard.totalActiveCars;
    document.getElementById('adminProfit').textContent = RR.money(dashboard.totalPlatformProfit);
    document.getElementById('adminLastReservations').innerHTML = (dashboard.last5Reservations || []).length
      ? dashboard.last5Reservations.map(item => `<li>${RR.escapeHtml(item)}</li>`).join('')
      : '<li class="muted">Nu există rezervări încă.</li>';
    await loadPendingCars();
    await loadPendingDocs();
  } catch (err) { RR.alertBox('adminAlert', err.message, false); }
  // --- Logică Popup Mașini Active ---
  const kpiActiveCarsBtn = document.getElementById('kpiActiveCarsBtn');
  const activeCarsModal = document.getElementById('activeCarsModal');
  const closeActiveCarsModal = document.getElementById('closeActiveCarsModal');
  
  if (kpiActiveCarsBtn && activeCarsModal) {
    kpiActiveCarsBtn.addEventListener('click', async () => {
      activeCarsModal.classList.remove('hidden');
      const tbody = document.getElementById('activeCarRows');
      tbody.innerHTML = '<tr><td colspan="5">Se încarcă...</td></tr>';
      try {
        const cars = await RR.api('/api/admin/cars/active', { headers: RR.headers(false) });
        tbody.innerHTML = cars.length ? cars.map(c => `<tr>
          <td>#${c.id}</td>
          <td>${RR.escapeHtml(c.brand)} ${RR.escapeHtml(c.model)}</td>
          <td>${RR.escapeHtml(c.location || '-')}</td>
          <td>${RR.escapeHtml(c.category || '-')}</td>
          <td>${RR.money(c.price)}</td>
        </tr>`).join('') : '<tr><td colspan="5" class="muted">Nu există mașini active în platformă.</td></tr>';
      } catch (err) { tbody.innerHTML = `<tr><td colspan="5" style="color:red">${err.message}</td></tr>`; }
    });
    closeActiveCarsModal.addEventListener('click', () => activeCarsModal.classList.add('hidden'));
    activeCarsModal.addEventListener('click', (e) => { if (e.target === activeCarsModal) activeCarsModal.classList.add('hidden'); });
  }

  const kpiProfitBtn = document.getElementById('kpiProfitBtn');
  const profitChartModal = document.getElementById('profitChartModal');
  const closeProfitModal = document.getElementById('closeProfitModal');
  let profitChartInstance = null; 

  if (kpiProfitBtn && profitChartModal) {
    kpiProfitBtn.addEventListener('click', async () => {
      profitChartModal.classList.remove('hidden');
      try {
        const data = await RR.api('/api/admin/cars/finance/chart', { headers: RR.headers(false) });
        const ctx = document.getElementById('profitChartCanvas').getContext('2d');
        
        if (profitChartInstance) profitChartInstance.destroy(); 
        
        profitChartInstance = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: data.map(d => d.month),
            datasets: [{
              label: 'Profit încasat (RON)',
              data: data.map(d => d.profit),
              backgroundColor: '#006ce4',
              borderRadius: 6
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: { y: { beginAtZero: true } },
            plugins: { legend: { display: false } }
          }
        });
      } catch (err) { alert('Eroare grafic: ' + err.message); }
    });
    closeProfitModal.addEventListener('click', () => profitChartModal.classList.add('hidden'));
    profitChartModal.addEventListener('click', (e) => { if (e.target === profitChartModal) profitChartModal.classList.add('hidden'); });
  }
}

async function loadPendingCars() {
  const cars = await RR.api('/api/admin/cars/pending', { headers: RR.headers(false) });
  const tbody = document.getElementById('pendingCarRows');
  tbody.innerHTML = cars.length ? cars.map(c => `<tr><td>#${c.id}</td><td>${RR.escapeHtml(c.brand)} ${RR.escapeHtml(c.model)}</td><td>${RR.escapeHtml(c.location || '-')}</td><td>${RR.money(c.price)}</td><td><button class="btn btn-success" data-car-action="APPROVE" data-car-id="${c.id}">Aprobă</button> <button class="btn btn-danger" data-car-action="REJECT" data-car-id="${c.id}">Respinge</button></td></tr>`).join('') : '<tr><td colspan="5" class="muted">Nu există mașini în așteptare.</td></tr>';
  tbody.querySelectorAll('[data-car-action]').forEach(btn => btn.addEventListener('click', async () => {
    try { const msg = await RR.api(`/api/admin/cars/moderate/${btn.dataset.carId}?action=${btn.dataset.carAction}`, { method: 'POST', headers: RR.headers(false) }); RR.alertBox('adminAlert', msg, true); await loadPendingCars(); }
    catch (err) { RR.alertBox('adminAlert', err.message, false); }
  }));
}

async function loadPendingDocs() {
  const docs = await RR.api('/api/admin/cars/documents/pending', { headers: RR.headers(false) });
  const tbody = document.getElementById('pendingDocumentRows');
  tbody.innerHTML = docs.length ? docs.map(d => `<tr><td>#${d.id}</td><td>${RR.escapeHtml(d.username)}</td><td>${RR.escapeHtml(d.email)}</td><td>${d.hasIdCardImage ? 'CI încărcat' : 'CI lipsă'} / ${d.hasDriverLicenseImage ? 'Permis încărcat' : 'Permis lipsă'}</td><td><button class="btn btn-success" data-doc-action="APPROVE" data-client-id="${d.id}">Aprobă</button> <button class="btn btn-danger" data-doc-action="REJECT" data-client-id="${d.id}">Respinge</button></td></tr>`).join('') : '<tr><td colspan="5" class="muted">Nu există documente în așteptare.</td></tr>';
  tbody.querySelectorAll('[data-doc-action]').forEach(btn => btn.addEventListener('click', async () => {
    try { const msg = await RR.api(`/api/admin/cars/documents/moderate/${btn.dataset.clientId}?action=${btn.dataset.docAction}`, { method: 'POST', headers: RR.headers(false) }); RR.alertBox('adminAlert', msg, true); await loadPendingDocs(); }
    catch (err) { RR.alertBox('adminAlert', err.message, false); }
  }));
}

function initConfirmationPage() {
  const el = document.getElementById('reservationMessage');
  if (!el) return;
  el.textContent = sessionStorage.getItem('rapidrent.lastReservationMessage') || 'Rezervarea ta a fost procesată.';
}
