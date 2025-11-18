const API_BASE = 'https://safe-transfer-api-xxxxx.onrender.com';

function $(s) { return document.querySelector(s); }

async function safeJson(r) {
  try { return await r.json(); } catch { return null; }
}

async function validarPix(payload) {
  const r = await fetch(`${API_BASE}/validar-pix`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  const data = await safeJson(r);

  if (!r.ok) {
    const msg =
      data?.mensagem ||
      data?.errors?.map(e => `${e.field}: ${e.message}`).join('\n') ||
      data?.error ||
      'Erro ao validar Pix';
    throw new Error(msg);
  }

  return data; // { status, mensagem, nomeReal }
}

document.addEventListener('DOMContentLoaded', () => {
  console.log('✅ Script carregado e pronto.');

  const pixForm = $('#pixForm');
  const chaveInput = $('#chave');
  const nomeInput = $('#nomeInformado');
  const nomeRealInput = $('#nomeReal');
  const resultado = $('#resultado');
  const historico = $('#listaHistorico');

  pixForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    resultado.textContent = 'Validando...';
    resultado.style.color = '';
    nomeRealInput.value = '';

    const chavePix = chaveInput.value.trim();
    const nomeInformado = nomeInput.value.trim();

    if (!chavePix) {
      resultado.textContent = 'Informe a chave Pix.';
      resultado.style.color = 'red';
      return;
    }

    if (!nomeInformado) {
      resultado.textContent = 'Informe o nome.';
      resultado.style.color = 'red';
      return;
    }

    try {
      const resp = await validarPix({ chavePix, nomeInformado });
      // resp = { status, mensagem, nomeReal }

      // Preenche o nome real no campo de baixo
      nomeRealInput.value = resp.nomeReal || '';

      // Mostra status e mensagem
      resultado.textContent = `${resp.status} — ${resp.mensagem}`;
      resultado.style.color =
        resp.status === 'VÁLIDO' ? 'green'
      : resp.status === 'DIVERGENTE' ? 'red'
      : 'orange';

      // Adiciona ao histórico
      if (historico) {
        const li = document.createElement('li');
        li.textContent = `${new Date().toLocaleString()} → ${chavePix} → ${resp.status}`;
        historico.prepend(li);
      }

    } catch (err) {
      resultado.textContent = err.message;
      resultado.style.color = 'red';
    }
  });
});
