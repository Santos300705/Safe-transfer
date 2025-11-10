const API_BASE = '/api';

function $(s) { return document.querySelector(s); }
async function safeJson(r) { try { return await r.json(); } catch { return null; } }

// ========== API CALLS ==========
async function buscarNomePorChave(chavePix) {
  const r = await fetch(`${API_BASE}/pix/${encodeURIComponent(chavePix)}`);
  const data = await safeJson(r);
  if (!r.ok) throw new Error(data?.error || 'Chave Pix não encontrada');
  return data; // {chavePix, nomeReal}
}

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
  const nomeInput = $('#nome'); // nome que o usuário digita
  const nomeRealInput = $('#nomeReal'); // nome real do banco (readonly)
  const valorInput = $('#valor');
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

  // Busca automática do nome ao digitar a chave
  let debounceId;
  function debounce(fn, ms = 300) {
    clearTimeout(debounceId);
    debounceId = setTimeout(fn, ms);
  }

  chaveInput.addEventListener('input', () => debounce(buscarNome));
  chaveInput.addEventListener('blur', buscarNome);

  async function buscarNome() {
    const chave = chaveInput.value.trim();
    if (!chave) return;
    nomeRealInput.value = 'Buscando...';
    try {
      const { nomeReal } = await buscarNomePorChave(chave);
      nomeRealInput.value = nomeReal; // <-- preenche o campo readonly
    } catch {
      nomeRealInput.value = '❌ Chave não encontrada';
    }
  }

  // Validação principal
  pixForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    resultado.textContent = 'Validando...';
    resultado.style.color = '';

    const chavePix = chaveInput.value.trim();
    const nomeInformado = nomeInput.value.trim(); // <-- nome digitado pelo usuário
    const valor = parseFloat(valorInput.value);
    const usuarioId = 1;

    try {
      const resp = await validarPix({ chavePix, nomeInformado, valor, usuarioId });
      resultado.textContent = `${resp.status} — ${resp.mensagem}`;
      resultado.style.color = resp.status === 'VÁLIDO' ? 'green' : 'red';

      // Adiciona ao histórico
      const li = document.createElement('li');
      li.textContent = `${new Date().toLocaleString()} → ${chavePix} → ${resp.status}`;
      historico.prepend(li);
    } catch (err) {
      resultado.textContent = err.message;
      resultado.style.color = 'red';
    }
  });
});