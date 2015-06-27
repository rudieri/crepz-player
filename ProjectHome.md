# Crepz Player #
O trabalho desenvolvido foi proposto pela disciplina de linguagem de programação II a idéia inicial era de um player de áudio que reproduzisse arquivos do formato mp3, mas na evolução do desenvolvimento outras ideias foram inseridas no projeto, como manipular arquivos em uma playlist, gerenciar música através de uma biblioteca, uso de banco de dados para armazenar informações.

Começamos pelo objetivo do trabalho, que era reproduzir arquivos no formato mp3. Começamos com o JMF (Java Media FrameWork ) entretanto este já era obsoleto, assim, outro meio deveria ser encontrado. Pelas indicações de usuários de fóruns de discussões, usamos as bibliotecas do javazoom, nessas, é possível manipular as funções básicas do player, como por exemplo, play, pause, controle de volume e balanço.

O Segundo recurso que foi desenvolvido foi o editor de propriedades, este que permite edição das tags diretamente no arquivo. Tags está que armazenam os dados do nome da musica, artista, álbum, gênero, etc. A partir destes dados foram feitos a modelagem do objeto musica.

No decorrer do trabalho tivemos algumas dificuldades como manipular os dados através de SQL sem instalação de um SGBD como serviço, com acesso direto aos arquivos, executar os arquivos de áudio de forma fluida, modelagem dos objetos e banco de dados e interface amigável e fácil de usar.

Para a solução do problema do banco de dados, foi tentado inicialmente utilizar o acess, ou arquivos mdb, porem é necessário configuração de fonte de dados ODBC para o funcionamento, assim após uma incansável pesquisa, encontramos o HSQL (Hiper SQL) banco de dados desenvolvido totalmente em Java, capaz de salvar e executar seus scripts diretamente em arquivos, iniciando o serviço apenas em tempo de execução, porém, também é capaz também de ser executado como serviço como os demais SGBD’s.

A biblioteca serve para importar os arquivos de música e organizá-los conforme as preferências do usuário. Quando uma música é importada, os dados que estão no seu cabeçalho são lidos e gravados no banco. Os dados gravados são o nome da música, o nome da banda ou artista, o gênero, o álbum e o endereço. Assim não é necessário ler as informações da música no arquivo toda vez que queremos fazer uma busca ou adicioná-los a lista de reprodução.

Temos também a lista de reprodução, que nada mais é que uma lista das músicas que irão tocar. Ela tem algumas funções como salvar sua lista de reprodução internamente (no banco), exportar como M3U (lista de reprodução em um arquivo), ler arquivos M3U.