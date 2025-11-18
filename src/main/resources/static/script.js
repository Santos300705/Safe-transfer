console.log("Script carregado e pronto.");

const form = document.getElementById("pixForm");
const inputChave = document.getElementById("chave");
const inputNomeInformado = document.getElementById("nomeInformado");
const inputNomeReal = document.getElementById("nomeReal");
const mensagem = document.getElementById("mensagemValidacao");

const API_URL = "https://safe-transfer-api-1234.onrender.com/api/validar-pix";

form.addEventListener("submit", async (event) => {
  event.preventDefault();

  const chavePix = inputChave.value.trim();
  const nomeInformado = inputNomeInformado.value.trim();

  mensagem.textContent = "";
  inputNomeReal.value = "";

  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        chavePix: chavePix,
        nomeInformado: nomeInformado
      })
    });

    const data = await response.json();

    // Se vier nomeReal, preenche o campo
    if (data.nomeReal) {
      inputNomeReal.value = data.nomeReal;
    }

    // Mostra mensagem
    mensagem.textContent = data.mensagem;

    // Pinta de verde/vermelho conforme status
    if (data.status === "VÁLIDO") {
      mensagem.style.color = "green";
    } else if (data.status === "DIVERGENTE") {
      mensagem.style.color = "orange";
    } else {
      // ERRO
      mensagem.style.color = "red";
    }

  } catch (erro) {
    console.error("Erro ao chamar API:", erro);
    mensagem.textContent = "Erro ao comunicar com o servidor.";
    mensagem.style.color = "red";
  }
});
