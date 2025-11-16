const API_BASE = '/api';

function $(s) { return document.querySelector(s); }
async function safeJson(r) { try { return await r.json(); } catch { return null; } }

// ========== API CALLS ==========
async function validarPix(payload) {
  const r = await fetch(`${API_BASE}/validar-pix`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  const data = await safeJson(r);
  if (!r.ok) {
    const msg = data?.errors?.map(e => `${e.field}: ${e.message}`).join('\n')
             || data?.error || 'Erro ao validar PIX';
    throw new Error(msg);
  }
  return data;
}

// ========== EVENTOS ==========
document.addEventListener('DOMContentLoaded', () => {
  console.log('✅ Script carregado e pronto.');

  const pixForm = $('#pixForm');
  const chaveInput = $('#chave');
  const nomeRealInput = $('#nomeReal'); // nome real do banco (readonly)
  const resultado = $('#resultado');
  const historico = $('#listaHistorico');
  const tabs = document.querySelectorAll('.tab-button');

  // Tabs
  tabs.forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.tab-button').forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(sec => sec.classList.remove('active'));
      btn.classList.add('active');
      document.getElementById(btn.dataset.tab).classList.add('active');
    });
  });

  // Validação principal
  pixForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    resultado.textContent = 'Validando...';
    resultado.style.color = '';
    nomeRealInput.value = '';

    const chavePix = chaveInput.value.trim();
    const usuarioId = 1;

    try {
      const resp = await validarPix({ chavePix, usuarioId });

      nomeRealInput.value = resp.nomeReal || '—';
      resultado.textContent = `${resp.status} — ${resp.mensagem}`;
      resultado.style.color = resp.status === 'VÁLIDO' ? 'green' : 'red';

      // Adiciona ao histórico
      const li = document.createElement('li');
      const nomeMarcado = resp.nomeReal ? ` (${resp.nomeReal})` : '';
      li.textContent = `${new Date().toLocaleString()} → ${chavePix}${nomeMarcado} → ${resp.status}`;
      historico.prepend(li);
    } catch (err) {
      resultado.textContent = err.message;
      resultado.style.color = 'red';
    }
  });
});