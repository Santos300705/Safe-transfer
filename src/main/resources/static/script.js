const API_BASE = '/api';

function $(s) {
  return document.querySelector(s);
}

async function safeJson(r) {
  try {
    return await r.json();
  } catch {
    return null;
  }
}

// ======== API CALLS ========

// POST /api/validar-pix
// Body: { chavePix, nomeInformado, usuarioId }
// Resp: { status, mensagem, nomeReal }
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
  const nomeInput = $('#nome');         // nome digitado pelo usuário
  const nomeRealInput = $('#nomeReal'); // nome real retornado pelo back
  const resultado = $('#resultado');
  const historico = $('#listaHistorico');
  const tabs = document.querySelectorAll('.tab-button');

  // ----- Tabs (Home / Histórico / Conta) -----
  tabs.forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.tab-button').forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(sec => sec.classList.remove('active'));
      btn.classList.add('active');
      const tabId = btn.dataset.tab;
      const tabContent = document.getElementById(tabId);
      if (tabContent) tabContent.classList.add('active');
    });
  });

  // ----- Submit do formulário de validação -----
  pixForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    resultado.textContent = 'Validando...';
    resultado.style.color = '';
    if (nomeRealInput) nomeRealInput.value = '';

    const chavePix = chaveInput.value.trim();
    const nomeInformado = nomeInput.value.trim();
    const usuarioId = 1; // ajusta se precisar

    if (!chavePix) {
      resultado.textContent = 'Informe a chave Pix.';
      resultado.style.color = 'red';
      return;
    }

    if (!nomeInformado) {
      resultado.textContent = 'Informe o nome que você recebeu.';
      resultado.style.color = 'red';
      return;
    }

    try {
      const resp = await validarPix({ chavePix, nomeInformado, usuarioId });
      // resp = { status: "VÁLIDO"/"DIVERGENTE", mensagem, nomeReal }

      if (nomeRealInput) {
        nomeRealInput.value = resp.nomeReal || '';
      }

      resultado.textContent = `${resp.status} — ${resp.mensagem}`;
      resultado.style.color =
        resp.status === 'VÁLIDO' ? 'green' : 'red';

      // Histórico
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
