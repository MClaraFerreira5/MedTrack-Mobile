<div align="center" justify="center">
    <img width="15%" src="assets/logo-medtrack.png" alt="Logo do MedTrack"> 
    <h1>MedTrack: Aplicação Mobile </h1>
</div>

> Aplicativo Android para controle inteligente de medicação via OCR e notificações

## Visão Geral

<div align="center">
  <img src="assets/app-preview.gif" width="30%" alt="Demonstração do MedTrack">
</div>

O **MedTrack Mobile** é um app Android desenvolvido para auxiliar no acompanhamento correto de medicamentos, unindo **OCR, notificações e acessibilidade** em um só lugar.

- 🔔 **Notificações inteligentes**
- 📸 **Validação por foto** usando tecnologia OCR
- ♿ **Acessibilidade** como prioridade

**Público-alvo:**
- 👴 Idosos e pacientes com muitos rémedios que dificulte a organização
- 🧑‍⚕️ Cuidadores e familiares para monitoramento

## ✨ Destaques Técnicos

### 🏗️ Arquitetura do Projeto
O MedTrack foi desenvolvido seguindo os princípios do **MVVM (Model-View-ViewModel)** para garantir uma separação clara de responsabilidades e facilitar a manutenção do código. Utilizamos componentes modernos do Android Jetpack como:
- ViewModel para gerenciamento de dados da UI
- LiveData para atualizações reativas
- Coroutines para operações assíncronas

<div align="center">
  <img src="assets/mvvm-diagram.png" width="100%" alt="Diagrama MVVM">
</div>

### 🎨 Interface Gráfica
Desenvolvida inteiramente com **Jetpack Compose**, a interface prioriza:
- Design moderno e intuitivo
- Acessibilidade

> ⏰ **Lista inteligente de horários**  
> - 💊 Contínuo (emoji de infinito 🔄)  
> - ⏳ Temporário (emoji de calendário 📅)

<div align="center">
  <img src="assets/screen-3.jpg" width="30%" alt="Lista de horários vazia">
  <img src="assets/screen-4.jpg" width="30%" alt="Lista de horários completa">
</div>

> 💡 **Pop-ups intuitivos**

<div align="center">
  <img src="assets/screen-2.jpg" width="30%" alt="Pop-up Editar">
  <img src="assets/screen-1.jpg" width="30%" alt="Pop-up Erro">
  <img src="assets/screen-5.jpg" width="30%" alt="Pop-up Sucesso">
</div>

### 📸 Captura e Reconhecimento (OCR)
Integramos as poderosas ferramentas do **Google ML Kit (*Text recognition* e *Object detection*)** para:
- **OCR (Reconhecimento Óptico de Caracteres)** para extrair dados de bulas e caixas de remédio
- **Detecção de objetos** para identificar medicamentos na câmera
- Processamento offline para garantir privacidade e disponibilidade

> 🎥 Demonstração do OCR identificando: nome do medicamento, dosagem e horários

<div align="center">
  <img src="assets/ocr-demo.gif" width="30%" alt="Demonstração do OCR">
</div>

### 💾 Armazenamento Local
Para persistência de dados, utilizamos:
- **Room Database** como camada de abstração sobre SQLite
- Armazenamento seguro de informações sensíveis
- Sincronização eficiente com o backend

```kotlin
@Database(
    entities = [Usuario::class, Medicamento::class, Notificacao::class, Confirmacao::class],
    version = 4
)
    abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
}
````

### 🌐 Comunicação com API
Integração com o backend através de:

- Retrofit para requisições HTTP

- Moshi para serialização/desserialização JSON

Tratamento robusto de erros e estados de carregamento:

````kotlin
interface ApiService {

    @POST("auth/mobile/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("usuario/mobile")
    suspend fun getUsuario(@Header("Authorization") token: String): Response<Usuario>

    @GET("medicamento/mobile/lista")
    suspend fun getMedicamentos(@Header("Authorization") token: String): Response<List<Medicamento>>

    @POST("/api/confirmacao")
    suspend fun confirmarMedicamento(
        @Header("Authorization") token: String,
        @Body request: DadosConfirmacaoRequest
    )

}
````

### 🔧 Outras Bibliotecas

- AlarmManager para agendamento de notificações
- Material3 para componentes UI modernos

## 🚀 Como Executar

1. **Pré-requisitos**:
    - Android Studio Giraffe+
    - Dispositivo/emulador com Android 9+

2. **Configuração**:
    - Clonar repositório
```bash
git clone https://github.com/seu-usuario/medtrack-mobile.git
````
> Configurar variáveis no ApiClient.kt para o endpoint do Backend:

```kotlin
  class ApiClient {
    private val BASE_URL = "http://seu-endpoint:8081"
  }
````

## 🌐 MedTrack: Versão Web

<div align="center">
  <a href="https://github.com/EllenRocha1/MedTrack" target="_blank">
    <img src="https://img.shields.io/badge/🔗_Acessar_Repositório-181717?style=for-the-badge&logo=github" alt="Repositório Web">
  </a>
</div>

### Plataforma Complementar
O **MedTrack Web** é a interface administrativa do sistema, desenvolvida para:

- 👩‍⚕️ **Profissionais de saúde** gerenciarem pacientes
- 👨‍👩‍👧 **Familiares** acompanharem a medicação remota
- 📊 Visualização de relatórios e histórico completo

<div align="center">
  <img src="assets/medtrack-web.png" width="100%" alt="Dashboard Web">
</div>

### Integração Mobile-Web
- 🔄 Sincronização em tempo real dos dados de medicação
- 🔐 Autenticação unificada JWT
- 📩 Notificações complementares via email

## 🌟 Time de Contribuidores

<div align="center">

<table>
  <tr>
    <td align="center">
        <img src="https://github.com/YannLeao.png" width="100px;" alt="Yann Leão"/><br />
        <sub><b>Yann Leão</b></sub>
      <br />
      <a href="https://github.com/YannLeao">
        <img src="https://img.shields.io/badge/-GitHub-181717?style=flat-square&logo=github" />
      </a>
      <a href="https://www.linkedin.com/in/yannleao-dev">
        <img src="https://img.shields.io/badge/-LinkedIn-0077B5?style=flat-square&logo=linkedin" />
      </a>
      <br />
      <code>Backend & Mobile</code>
    </td>
    <td align="center">
        <img src="https://github.com/EllenRocha1.png" width="100px;" alt="Ellen Rocha"/><br />
        <sub><b>Ellen Rocha</b></sub>
      <br />
      <a href="https://github.com/EllenRocha1">
        <img src="https://img.shields.io/badge/-GitHub-181717?style=flat-square&logo=github" />
      </a>
      <a href="https://www.linkedin.com/in/ellen-rocha-dev/">
        <img src="https://img.shields.io/badge/-LinkedIn-0077B5?style=flat-square&logo=linkedin" />
      </a>
      <br />
      <code>Backend & Frontend</code>
    </td>
    <td align="center">
        <img src="https://github.com/MClaraFerreira5.png" width="100px;" alt="Maria Clara"/><br />
        <sub><b>Maria Clara</b></sub>
      <br />
      <a href="https://github.com/MClaraFerreira5">
        <img src="https://img.shields.io/badge/-GitHub-181717?style=flat-square&logo=github" />
      </a>
      <a href="https://www.linkedin.com/in/clara-ferreira-dev/">
        <img src="https://img.shields.io/badge/-LinkedIn-0077B5?style=flat-square&logo=linkedin" />
      </a>
      <br />
      <code>Frontend & Mobile</code>
    </td>
  </tr>
</table>

</div>

## 📄 Licença

Projeto acadêmico desenvolvido para a disciplina de **Projeto Interdisciplinar de Engenharia da Computação 1 (PIEC1)**  
Universidade Federal Rural de Pernambuco — Unidade Acadêmica de Belo Jardim (UFRPE/UABJ)




