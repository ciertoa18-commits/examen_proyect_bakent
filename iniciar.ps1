param(
    [string]$Url = 'jdbc:oracle:thin:@localhost:1522/FREEPDB1',
    [string]$Usuario = 'EquipAndess'
)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$claveOracle = Read-Host 'Contrasena de Oracle' -AsSecureString
$env:DB_URL = $Url
$env:DB_USERNAME = $Usuario
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $claveOracle).Password
try {
    & (Join-Path $PSScriptRoot 'mvnw.cmd') spring-boot:run '-Dspring-boot.run.profiles=dev'
    if ($LASTEXITCODE -ne 0) { throw 'La aplicacion no pudo arrancar; revisar los mensajes anteriores.' }
}
finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
