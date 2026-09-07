--[[
    Key System GUI + Syntrix API Integration
    Docs: https://dev.syntrx.xyz/docs

    Fluxo integrado:
      1. (Opcional) Auto-login com key salva em cache no workspace
      2. GET  /api/keys/challenge?scriptId=...  -> token de desafio
      3. POST /api/keys/validate                -> {key, scriptId, hwid, challenge, placeId, jobId}
      4. Sucesso: salva a key em cache, destroi a GUI e entrega a key ao loader (_SxSetKey)
]]

-- ========================================
-- SERVICES & INITIALIZATION
-- ========================================

local Services = {
    Players = game:GetService("Players"),
    TweenService = game:GetService("TweenService"),
    UserInputService = game:GetService("UserInputService"),
    RunService = game:GetService("RunService"),
    HttpService = game:GetService("HttpService")
}

local HttpService = Services.HttpService
local Player = Services.Players.LocalPlayer
local PlayerGui = Player:WaitForChild("PlayerGui")

-- ========================================
-- SYNTRIX KEY SYSTEM (CONFIG DA API)
-- ========================================

local Syntrix = {
    ScriptId    = "d6bc60135600150886d1a785",
    ValidateUrl = "https://dev.syntrx.xyz/api/keys/validate",
    GetKeyUrl   = "https://dev.syntrx.xyz/api/keys/getkey/d6bc60135600150886d1a785",

    -- Configs extras da integracao
    CacheKey       = true,                                            -- salvar key valida no workspace (auto-login na proxima execucao)
    CacheFileName  = "syntrx_key_d6bc60135600150886d1a785.txt",       -- arquivo de cache
    DiscordInvite  = "https://discord.gg/your-invite",                -- troque pelo invite do SEU servidor
}

-- URL do challenge derivada da URL de validate (igual ao exemplo oficial)
Syntrix.ChallengeUrl = Syntrix.ValidateUrl:gsub("/keys/validate", "/keys/challenge") .. "?scriptId=" .. Syntrix.ScriptId

-- ========================================
-- CONFIGURATION
-- ========================================

local Config = {
    MaxKeyLength = 128, -- aumentado: keys da Syntrix podem passar de 50 chars
    AnimationSpeed = 0.4,
    ParticleCount = 60,
    ParticleSpeed = 60
}

-- ========================================
-- COLOR SCHEME
-- ========================================

local Colors = {
    Background = Color3.fromRGB(18, 18, 22),
    Surface = Color3.fromRGB(25, 25, 30),
    Primary = Color3.fromRGB(45, 45, 50),
    Secondary = Color3.fromRGB(35, 35, 40),
    Border = Color3.fromRGB(40, 40, 45),
    TextPrimary = Color3.fromRGB(220, 220, 225),
    TextSecondary = Color3.fromRGB(140, 140, 150),
    Success = Color3.fromRGB(25, 135, 84),
    Error = Color3.fromRGB(180, 50, 50),
    Warning = Color3.fromRGB(200, 120, 30),
    Discord = Color3.fromRGB(60, 70, 180),
    GetKey = Color3.fromRGB(40, 140, 100),
    HoverPrimary = Color3.fromRGB(55, 55, 60),
    HoverDiscord = Color3.fromRGB(50, 60, 160),
    HoverGetKey = Color3.fromRGB(30, 120, 80),
    NeonWhite = Color3.fromRGB(255, 255, 255),
    NeonGlow = Color3.fromRGB(240, 248, 255)
}

-- ========================================
-- STATE MANAGEMENT
-- ========================================

local State = {
    IsLoading = false,
    Particles = {},
    Animations = {},
    IsDestroyed = false,
    MousePosition = {X = 0, Y = 0},
    FocusStates = {
        InputFocused = false,
        ButtonHovered = {},
        AnimationsActive = true
    }
}

local UI = {}

-- ========================================
-- SYNTRIX: HWID / REQUEST / VALIDATE / CACHE
-- ========================================

local KeySystem = {}

function KeySystem.GetHWID()
    local hwid = ""
    pcall(function()
        hwid = game:GetService("RbxAnalyticsService"):GetClientId()
    end)
    if type(hwid) ~= "string" or hwid == "" then
        hwid = tostring(Player.UserId)
    end
    return hwid
end

function KeySystem.GetRequestFunction()
    local candidates = {
        syn and syn.request,
        http_request,
        request,
        fluxus and fluxus.request,
    }
    for _, fn in ipairs(candidates) do
        if type(fn) == "function" then return fn end
    end
    return nil
end

-- Passo 1: buscar o challenge token da sessao
function KeySystem.FetchChallenge()
    local ok, body = pcall(function()
        local reqFn = KeySystem.GetRequestFunction()
        if reqFn then
            local res = reqFn({Url = Syntrix.ChallengeUrl, Method = "GET"})
            if type(res) == "table" and type(res.Body) == "string" then
                return res.Body
            end
            return nil
        end
        return game:HttpGet(Syntrix.ChallengeUrl)
    end)

    if not ok or type(body) ~= "string" then
        return nil, "Challenge failed"
    end

    local decoded
    pcall(function() decoded = HttpService:JSONDecode(body) end)
    if type(decoded) ~= "table" or not decoded.challenge then
        return nil, "Challenge failed"
    end
    return decoded.challenge
end

-- Passo 2: validar a key contra a API
function KeySystem.Validate(key)
    local reqFn = KeySystem.GetRequestFunction()
    if not reqFn then
        return {success = false, error = "Your executor does not support HTTP requests"}
    end

    local challenge, chalErr = KeySystem.FetchChallenge()
    if not challenge then
        return {success = false, error = chalErr or "Challenge failed"}
    end

    local ok, res = pcall(reqFn, {
        Url = Syntrix.ValidateUrl,
        Method = "POST",
        Headers = {["Content-Type"] = "application/json"},
        Body = HttpService:JSONEncode({
            key = key,
            scriptId = Syntrix.ScriptId,
            hwid = KeySystem.GetHWID(),
            challenge = challenge,
            placeId = tostring(game.PlaceId),
            jobId = tostring(game.JobId),
        })
    })

    if not ok or type(res) ~= "table" or type(res.Body) ~= "string" then
        return {success = false, error = "Request failed, try again"}
    end

    local decoded
    pcall(function() decoded = HttpService:JSONDecode(res.Body) end)
    if type(decoded) ~= "table" then
        decoded = {success = (res.StatusCode == 200)}
    end

    if decoded.success == true then
        return {success = true, data = decoded}
    end

    local message = decoded.error or decoded.message or decoded.msg
    if type(message) ~= "string" or message == "" then
        message = "Invalid key (HTTP " .. tostring(res.StatusCode) .. ")"
    end
    return {success = false, error = message}
end

-- Cache da key no workspace (auto-login nas proximas execuçoes)
local KeyCache = {}

function KeyCache.Save(key)
    if not Syntrix.CacheKey then return end
    pcall(function()
        if type(writefile) == "function" then
            writefile(Syntrix.CacheFileName, key)
        end
    end)
end

function KeyCache.Load()
    local key
    pcall(function()
        if type(isfile) == "function" and type(readfile) == "function" and isfile(Syntrix.CacheFileName) then
            key = readfile(Syntrix.CacheFileName)
        end
    end)
    if type(key) == "string" then
        key = key:match("^%s*(.-)%s*$")
        if key ~= "" then return key end
    end
    return nil
end

function KeyCache.Clear()
    pcall(function()
        if type(delfile) == "function" and type(isfile) == "function" and isfile(Syntrix.CacheFileName) then
            delfile(Syntrix.CacheFileName)
        end
    end)
end

-- ========================================
-- UI CREATION FUNCTIONS
-- ========================================

local function CreateMainGUI()
    local screenGui = Instance.new("ScreenGui")
    screenGui.Name = "KeySystemGUI"
    screenGui.ResetOnSpawn = false
    screenGui.ZIndexBehavior = Enum.ZIndexBehavior.Sibling
    screenGui.IgnoreGuiInset = true
    screenGui.DisplayOrder = 100
    screenGui.Parent = PlayerGui
    
    UI.ScreenGui = screenGui
    return screenGui
end

local function CreateBackdrop(parent)
    local backdrop = Instance.new("Frame")
    backdrop.Name = "Backdrop"
    backdrop.Size = UDim2.new(1, 0, 1, 0)
    backdrop.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
    backdrop.BackgroundTransparency = 0.1
    backdrop.BorderSizePixel = 0
    backdrop.ZIndex = 100
    backdrop.Parent = parent
    
    UI.Backdrop = backdrop
    return backdrop
end

local function CreateContainer(parent)
    local container = Instance.new("Frame")
    container.Name = "MainContainer"
    container.Size = UDim2.new(0, 420, 0, 600)
    container.Position = UDim2.new(0.5, -210, 0.5, -300)
    container.BackgroundColor3 = Colors.Background
    container.BorderSizePixel = 0
    container.ZIndex = 110
    container.Selectable = false
    container.Parent = parent
    
    local corner = Instance.new("UICorner")
    corner.CornerRadius = UDim.new(0, 20)
    corner.Parent = container
    
    local stroke = Instance.new("UIStroke")
    stroke.Color = Colors.Border
    stroke.Thickness = 1
    stroke.Transparency = 0.3
    stroke.Parent = container
    
    UI.Container = container
    return container
end

local function CreateAnimatedBorder(parent)
    local border = Instance.new("Frame")
    border.Name = "AnimatedBorder"
    border.Size = UDim2.new(1, 6, 1, 6)
    border.Position = UDim2.new(0, -3, 0, -3)
    border.BackgroundTransparency = 1
    border.ZIndex = 109
    border.Selectable = false
    border.Parent = parent
    
    local corner = Instance.new("UICorner")
    corner.CornerRadius = UDim.new(0, 23)
    corner.Parent = border
    
    local stroke = Instance.new("UIStroke")  
    stroke.Color = Colors.NeonWhite
    stroke.Thickness = 2
    stroke.Transparency = 0.3
    stroke.Parent = border
    
    local gradient = Instance.new("UIGradient")
    gradient.Color = ColorSequence.new{
        ColorSequenceKeypoint.new(0, Colors.NeonWhite),
        ColorSequenceKeypoint.new(0.5, Colors.NeonGlow),
        ColorSequenceKeypoint.new(1, Colors.NeonWhite)
    }
    gradient.Transparency = NumberSequence.new{
        NumberSequenceKeypoint.new(0, 0.9),
        NumberSequenceKeypoint.new(0.2, 0.1),
        NumberSequenceKeypoint.new(0.8, 0.1),
        NumberSequenceKeypoint.new(1, 0.9)
    }
    gradient.Parent = stroke
    
    UI.AnimatedBorder = {Frame = border, Gradient = gradient, Stroke = stroke}
    return border
end

-- ========================================
-- HEADER SECTION
-- ========================================

local function CreateHeader(parent)
    local header = Instance.new("Frame")
    header.Name = "Header"
    header.Size = UDim2.new(1, 0, 0, 100)
    header.BackgroundTransparency = 1
    header.ZIndex = 11
    header.Selectable = false
    header.Parent = parent
    
    local iconContainer = Instance.new("Frame")
    iconContainer.Size = UDim2.new(0, 56, 0, 56)
    iconContainer.Position = UDim2.new(0.5, -28, 0, 24)
    iconContainer.BackgroundColor3 = Colors.Primary
    iconContainer.BorderSizePixel = 0
    iconContainer.ZIndex = 12
    iconContainer.Selectable = false
    iconContainer.Parent = header
    
    local iconCorner = Instance.new("UICorner")
    iconCorner.CornerRadius = UDim.new(0, 14)
    iconCorner.Parent = iconContainer
    
    local iconGlow = Instance.new("Frame")
    iconGlow.Size = UDim2.new(1, 12, 1, 12)
    iconGlow.Position = UDim2.new(0, -6, 0, -6)
    iconGlow.BackgroundTransparency = 1
    iconGlow.ZIndex = 11
    iconGlow.Selectable = false
    iconGlow.Parent = iconContainer
    
    local glowCorner = Instance.new("UICorner")
    glowCorner.CornerRadius = UDim.new(0, 20)
    glowCorner.Parent = iconGlow
    
    local glowStroke = Instance.new("UIStroke")
    glowStroke.Color = Colors.NeonWhite
    glowStroke.Thickness = 3
    glowStroke.Transparency = 0.2
    glowStroke.Parent = iconGlow
    
    local glowGradient = Instance.new("UIGradient")
    glowGradient.Color = ColorSequence.new{
        ColorSequenceKeypoint.new(0, Colors.NeonWhite),
        ColorSequenceKeypoint.new(0.5, Colors.NeonGlow),
        ColorSequenceKeypoint.new(1, Colors.NeonWhite)
    }
    glowGradient.Transparency = NumberSequence.new{
        NumberSequenceKeypoint.new(0, 0.8),
        NumberSequenceKeypoint.new(0.2, 0.05),
        NumberSequenceKeypoint.new(0.8, 0.05),
        NumberSequenceKeypoint.new(1, 0.8)
    }
    glowGradient.Parent = glowStroke
    
    local iconImage = Instance.new("ImageLabel")
    iconImage.Size = UDim2.new(0.8, 0, 0.8, 0)
    iconImage.Position = UDim2.new(0.1, 0, 0.1, 0)
    iconImage.BackgroundTransparency = 1
    iconImage.Image = "rbxassetid://95233466475324"
    iconImage.ImageColor3 = Colors.NeonWhite
    iconImage.ImageTransparency = 0.1
    iconImage.ScaleType = Enum.ScaleType.Fit
    iconImage.ZIndex = 13
    iconImage.Parent = iconContainer
    
    UI.Header = {Container = header, IconGlow = glowGradient, IconStroke = glowStroke}
    return header
end

-- ========================================
-- CONTENT SECTION
-- ========================================

local function CreateContent(parent)
    local content = Instance.new("Frame")
    content.Name = "Content"
    content.Size = UDim2.new(1, -64, 0, 440)
    content.Position = UDim2.new(0, 32, 0, 120)
    content.BackgroundTransparency = 1
    content.ZIndex = 11
    content.Selectable = false
    content.Parent = parent
    
    local title = Instance.new("TextLabel")
    title.Size = UDim2.new(1, 0, 0, 32)
    title.BackgroundTransparency = 1
    title.Text = "Access Key Required"
    title.TextColor3 = Colors.TextPrimary
    title.TextSize = 24
    title.Font = Enum.Font.GothamBold
    title.TextXAlignment = Enum.TextXAlignment.Center
    title.ZIndex = 12
    title.Parent = content
    
    local subtitle = Instance.new("TextLabel")
    subtitle.Size = UDim2.new(1, 0, 0, 40)
    subtitle.Position = UDim2.new(0, 0, 0, 40)
    subtitle.BackgroundTransparency = 1
    subtitle.Text = "Enter your access key to continue"
    subtitle.TextColor3 = Colors.TextSecondary
    subtitle.TextSize = 16
    subtitle.Font = Enum.Font.Gotham
    subtitle.TextXAlignment = Enum.TextXAlignment.Center
    subtitle.TextWrapped = true
    subtitle.ZIndex = 12
    subtitle.Parent = content
    
    UI.Content = content
    return content
end

-- ========================================
-- INPUT SECTION
-- ========================================

local function CreateInputSection(parent)
    local section = Instance.new("Frame")
    section.Size = UDim2.new(1, 0, 0, 100)
    section.Position = UDim2.new(0, 0, 0, 100)
    section.BackgroundTransparency = 1
    section.ZIndex = 12
    section.Selectable = false
    section.Parent = parent
    
    local inputContainer = Instance.new("Frame")
    inputContainer.Size = UDim2.new(1, 0, 0, 52)
    inputContainer.BackgroundColor3 = Colors.Surface
    inputContainer.BorderSizePixel = 0
    inputContainer.ZIndex = 13
    inputContainer.Selectable = false
    inputContainer.Parent = section
    
    local corner = Instance.new("UICorner")
    corner.CornerRadius = UDim.new(0, 12)
    corner.Parent = inputContainer
    
    local stroke = Instance.new("UIStroke")
    stroke.Color = Colors.Border
    stroke.Thickness = 1
    stroke.Transparency = 0.3
    stroke.Parent = inputContainer
    
    local inputGlow = Instance.new("Frame")
    inputGlow.Size = UDim2.new(1, 8, 1, 8)
    inputGlow.Position = UDim2.new(0, -4, 0, -4)
    inputGlow.BackgroundTransparency = 1
    inputGlow.ZIndex = inputContainer.ZIndex - 1
    inputGlow.Visible = false
    inputGlow.Selectable = false
    inputGlow.Parent = inputContainer
    
    local glowCorner = Instance.new("UICorner")
    glowCorner.CornerRadius = UDim.new(0, 16)
    glowCorner.Parent = inputGlow
    
    local glowStroke = Instance.new("UIStroke")
    glowStroke.Color = Colors.NeonWhite
    glowStroke.Thickness = 2
    glowStroke.Transparency = 0.3
    glowStroke.Parent = inputGlow
    
    local glowGradient = Instance.new("UIGradient")
    glowGradient.Color = ColorSequence.new{
        ColorSequenceKeypoint.new(0, Colors.NeonWhite),
        ColorSequenceKeypoint.new(0.5, Colors.NeonGlow),
        ColorSequenceKeypoint.new(1, Colors.NeonWhite)
    }
    glowGradient.Transparency = NumberSequence.new{
        NumberSequenceKeypoint.new(0, 0.8),
        NumberSequenceKeypoint.new(0.2, 0.1),
        NumberSequenceKeypoint.new(0.8, 0.1),
        NumberSequenceKeypoint.new(1, 0.8)
    }
    glowGradient.Parent = glowStroke
    
    local textInput = Instance.new("TextBox")
    textInput.Size = UDim2.new(1, -24, 1, 0)
    textInput.Position = UDim2.new(0, 12, 0, 0)
    textInput.BackgroundTransparency = 1
    textInput.Text = ""
    textInput.PlaceholderText = "Enter key here"
    textInput.TextColor3 = Colors.TextPrimary
    textInput.PlaceholderColor3 = Colors.TextSecondary
    textInput.TextSize = 16
    textInput.Font = Enum.Font.Gotham
    textInput.TextXAlignment = Enum.TextXAlignment.Left
    textInput.ClearTextOnFocus = false
    textInput.ZIndex = 14
    textInput.Selectable = true
    textInput.Parent = inputContainer
    
    local charCounter = Instance.new("TextLabel")
    charCounter.Size = UDim2.new(0, 80, 0, 20)
    charCounter.Position = UDim2.new(1, -85, 0, 60)
    charCounter.BackgroundTransparency = 1
    charCounter.Text = "0/" .. Config.MaxKeyLength
    charCounter.TextColor3 = Colors.TextSecondary
    charCounter.TextSize = 12
    charCounter.Font = Enum.Font.Gotham
    charCounter.TextXAlignment = Enum.TextXAlignment.Right
    charCounter.ZIndex = 13
    charCounter.Parent = section
    
    UI.Input = {
        Container = inputContainer,
        TextBox = textInput,
        Counter = charCounter,
        Stroke = stroke,
        Glow = {Frame = inputGlow, Stroke = glowStroke, Gradient = glowGradient}
    }
    
    return section
end

-- ========================================
-- BUTTON SECTION
-- ========================================

local function CreateButtons(parent)
    local submitButton = Instance.new("TextButton")
    submitButton.Size = UDim2.new(1, 0, 0, 48)
    submitButton.Position = UDim2.new(0, 0, 0, 200)
    submitButton.BackgroundColor3 = Colors.Primary
    submitButton.BorderSizePixel = 0
    submitButton.Text = "Verify Access Key"
    submitButton.TextColor3 = Colors.TextPrimary
    submitButton.TextSize = 16
    submitButton.Font = Enum.Font.GothamMedium
    submitButton.AutoButtonColor = false
    submitButton.ZIndex = 13
    submitButton.Selectable = true
    submitButton.Parent = parent
    
    local submitCorner = Instance.new("UICorner")
    submitCorner.CornerRadius = UDim.new(0, 12)
    submitCorner.Parent = submitButton
    
    local loadingContainer = Instance.new("Frame")
    loadingContainer.Size = UDim2.new(0, 24, 0, 24)
    loadingContainer.Position = UDim2.new(0.5, -12, 0, 12)
    loadingContainer.BackgroundTransparency = 1
    loadingContainer.Visible = false
    loadingContainer.ZIndex = 14
    loadingContainer.Selectable = false
    loadingContainer.Parent = submitButton
    
    local spinner = Instance.new("Frame")
    spinner.Size = UDim2.new(1, 0, 1, 0)
    spinner.BackgroundColor3 = Colors.TextPrimary
    spinner.BorderSizePixel = 0
    spinner.ZIndex = 15
    spinner.Selectable = false
    spinner.Parent = loadingContainer
    
    local spinnerCorner = Instance.new("UICorner")
    spinnerCorner.CornerRadius = UDim.new(1, 0)
    spinnerCorner.Parent = spinner
    
    local spinnerGradient = Instance.new("UIGradient")
    spinnerGradient.Transparency = NumberSequence.new{
        NumberSequenceKeypoint.new(0, 0),
        NumberSequenceKeypoint.new(0.8, 0.8),
        NumberSequenceKeypoint.new(1, 1)
    }
    spinnerGradient.Parent = spinner
    
    local buttonsContainer = Instance.new("Frame")
    buttonsContainer.Size = UDim2.new(1, 0, 0, 48)
    buttonsContainer.Position = UDim2.new(0, 0, 0, 260)
    buttonsContainer.BackgroundTransparency = 1
    buttonsContainer.ZIndex = 12
    buttonsContainer.Selectable = false
    buttonsContainer.Parent = parent
    
    local getKeyButton = Instance.new("TextButton")
    getKeyButton.Size = UDim2.new(0.48, 0, 1, 0)
    getKeyButton.BackgroundColor3 = Colors.GetKey
    getKeyButton.BorderSizePixel = 0
    getKeyButton.Text = "Get Key"
    getKeyButton.TextColor3 = Colors.TextPrimary
    getKeyButton.TextSize = 14
    getKeyButton.Font = Enum.Font.GothamMedium
    getKeyButton.AutoButtonColor = false
    getKeyButton.ZIndex = 13
    getKeyButton.Selectable = true
    getKeyButton.Parent = buttonsContainer
    
    local getKeyCorner = Instance.new("UICorner")
    getKeyCorner.CornerRadius = UDim.new(0, 10)
    getKeyCorner.Parent = getKeyButton
    
    local discordButton = Instance.new("TextButton")
    discordButton.Size = UDim2.new(0.48, 0, 1, 0)
    discordButton.Position = UDim2.new(0.52, 0, 0, 0)
    discordButton.BackgroundColor3 = Colors.Discord
    discordButton.BorderSizePixel = 0
    discordButton.Text = "Discord"
    discordButton.TextColor3 = Colors.TextPrimary
    discordButton.TextSize = 14
    discordButton.Font = Enum.Font.GothamMedium
    discordButton.AutoButtonColor = false
    discordButton.ZIndex = 13
    discordButton.Selectable = true
    discordButton.Parent = buttonsContainer
    
    local discordCorner = Instance.new("UICorner")
    discordCorner.CornerRadius = UDim.new(0, 10)
    discordCorner.Parent = discordButton
    
    UI.Buttons = {
        Submit = submitButton,
        GetKey = getKeyButton,
        Discord = discordButton,
        Loading = {Container = loadingContainer, Spinner = spinner}
    }
    
    return {submitButton, getKeyButton, discordButton}
end

-- ========================================
-- STATUS SECTION
-- ========================================

local function CreateStatus(parent)
    local statusContainer = Instance.new("Frame")
    statusContainer.Size = UDim2.new(1, 0, 0, 60)
    statusContainer.Position = UDim2.new(0, 0, 0, 330)
    statusContainer.BackgroundTransparency = 1
    statusContainer.ZIndex = 12
    statusContainer.Selectable = false
    statusContainer.Parent = parent
    
    local statusLabel = Instance.new("TextLabel")
    statusLabel.Size = UDim2.new(1, 0, 1, 0)
    statusLabel.BackgroundTransparency = 1
    statusLabel.Text = ""
    statusLabel.TextColor3 = Colors.Error
    statusLabel.TextSize = 14
    statusLabel.Font = Enum.Font.Gotham
    statusLabel.TextXAlignment = Enum.TextXAlignment.Center
    statusLabel.TextWrapped = true
    statusLabel.ZIndex = 13
    statusLabel.Parent = statusContainer
    
    UI.Status = statusLabel
    return statusLabel
end

-- ========================================
-- PARTICLE SYSTEM
-- ========================================

local function CreateParticleContainer(parent)
    local container = Instance.new("Frame")
    container.Size = UDim2.new(1, 0, 1, 0)
    container.BackgroundTransparency = 1
    container.ZIndex = 105
    container.Selectable = false
    container.Parent = parent
    
    UI.ParticleContainer = container
    return container
end

local function CreateParticle()
    if not UI.ParticleContainer or not UI.ParticleContainer.Parent or State.IsDestroyed then
        return nil
    end
    
    local size = math.random(8, 24)
    local particle = Instance.new("Frame")
    particle.Size = UDim2.new(0, size, 0, size)
    particle.Position = UDim2.new(math.random() * 1.4 - 0.2, 0, 1.2, 0)
    particle.BackgroundColor3 = Colors.NeonWhite
    particle.BackgroundTransparency = math.random(60, 85) / 100
    particle.BorderSizePixel = 0
    particle.ZIndex = 106
    particle.Selectable = false
    particle.Parent = UI.ParticleContainer
    
    local corner = Instance.new("UICorner")
    corner.CornerRadius = UDim.new(1, 0)
    corner.Parent = particle
    
    local gradient = Instance.new("UIGradient")
    local bubbleColors = {
        Color3.fromRGB(200, 230, 255),
        Color3.fromRGB(180, 220, 255),
        Color3.fromRGB(220, 240, 255),
        Color3.fromRGB(190, 210, 240)
    }
    local color1 = bubbleColors[math.random(#bubbleColors)]
    local color2 = bubbleColors[math.random(#bubbleColors)]
    
    gradient.Color = ColorSequence.new{
        ColorSequenceKeypoint.new(0, color1),
        ColorSequenceKeypoint.new(0.3, Color3.fromRGB(255, 255, 255)),
        ColorSequenceKeypoint.new(0.7, color2),
        ColorSequenceKeypoint.new(1, color1)
    }
    gradient.Rotation = math.random(0, 360)
    gradient.Parent = particle
    
    local highlight = Instance.new("Frame")
    highlight.Size = UDim2.new(0.3, 0, 0.3, 0)
    highlight.Position = UDim2.new(0.2, 0, 0.15, 0)
    highlight.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
    highlight.BackgroundTransparency = 0.3
    highlight.BorderSizePixel = 0
    highlight.ZIndex = particle.ZIndex + 1
    highlight.Parent = particle
    
    local highlightCorner = Instance.new("UICorner")
    highlightCorner.CornerRadius = UDim.new(1, 0)
    highlightCorner.Parent = highlight
    
    local glow = Instance.new("Frame")
    glow.Size = UDim2.new(1.8, 0, 1.8, 0)
    glow.Position = UDim2.new(-0.4, 0, -0.4, 0)
    glow.BackgroundColor3 = Color3.fromRGB(200, 230, 255)
    glow.BackgroundTransparency = 0.9
    glow.BorderSizePixel = 0
    glow.ZIndex = particle.ZIndex - 1
    glow.Parent = particle
    
    local glowCorner = Instance.new("UICorner")
    glowCorner.CornerRadius = UDim.new(1, 0)
    glowCorner.Parent = glow
    
    local particleData = {
        frame = particle,
        vx = (math.random() - 0.5) * 0.004,
        vy = -math.random(20, 50) / 10000,
        created = tick(),
        rotation = 0,
        rotationSpeed = (math.random() - 0.5) * 2,
        pulsePhase = math.random() * math.pi * 2,
        driftPhase = math.random() * math.pi * 2,
        originalTransparency = particle.BackgroundTransparency,
        glow = glow,
        highlight = highlight,
        lifetime = math.random(30, 60),
        originalSize = size,
        wobblePhase = math.random() * math.pi * 2,
        repelForce = {x = 0, y = 0},
        mass = size / 10
    }
    
    table.insert(State.Particles, particleData)
    return particle
end

local function UpdateParticles()
    if State.IsDestroyed or not UI.ParticleContainer then return end
    
    local screenSize = UI.ScreenGui.AbsoluteSize
    local mouseScreenX = State.MousePosition.X / screenSize.X
    local mouseScreenY = State.MousePosition.Y / screenSize.Y
    
    for i = #State.Particles, 1, -1 do
        local p = State.Particles[i]
        
        if not p or not p.frame or not p.frame.Parent then
            table.remove(State.Particles, i)
        else
            local currentPos = p.frame.Position
            local age = tick() - p.created
            
            if currentPos.Y.Scale < -0.3 or age > p.lifetime then
                p.frame:Destroy()
                table.remove(State.Particles, i)
            else
                local distanceToMouse = math.sqrt(
                    (currentPos.X.Scale - mouseScreenX)^2 + 
                    (currentPos.Y.Scale - mouseScreenY)^2
                )
                
                local repelStrength = 0.08
                local repelRadius = 0.15
                local repelForceX = 0
                local repelForceY = 0
                
                if distanceToMouse < repelRadius and distanceToMouse > 0 then
                    local repelPower = (repelRadius - distanceToMouse) / repelRadius
                    repelPower = repelPower * repelStrength / p.mass
                    
                    local directionX = (currentPos.X.Scale - mouseScreenX) / distanceToMouse
                    local directionY = (currentPos.Y.Scale - mouseScreenY) / distanceToMouse
                    
                    repelForceX = directionX * repelPower
                    repelForceY = directionY * repelPower
                end
                
                p.repelForce.x = p.repelForce.x * 0.85 + repelForceX * 0.15
                p.repelForce.y = p.repelForce.y * 0.85 + repelForceY * 0.15
                
                local newX = currentPos.X.Scale + p.vx + p.repelForce.x
                local newY = currentPos.Y.Scale + p.vy + p.repelForce.y
                
                if newX <= -0.2 then newX = 1.2
                elseif newX >= 1.2 then newX = -0.2 end
                
                local wobbleTime = tick() * 1.5 + p.wobblePhase
                newX = newX + math.sin(wobbleTime) * 0.002
                newY = newY + math.cos(wobbleTime * 0.7) * 0.001
                
                newX = newX + (math.random() - 0.5) * 0.0008
                newY = newY + (math.random() - 0.5) * 0.0005
                
                p.rotation = p.rotation + p.rotationSpeed
                p.frame.Rotation = p.rotation
                
                local breathe = math.sin(tick() * 2.5 + p.pulsePhase) * 0.1 + 1
                local currentSize = p.originalSize * breathe
                p.frame.Size = UDim2.new(0, currentSize, 0, currentSize)
                
                local transparencyPulse = math.sin(tick() * 3 + p.pulsePhase) * 0.1
                local newTransparency = math.max(0.5, math.min(0.95, p.originalTransparency + transparencyPulse))
                p.frame.BackgroundTransparency = newTransparency
                
                local glowIntensity = 0.9
                if distanceToMouse < 0.2 then
                    glowIntensity = 0.7 + (distanceToMouse / 0.2) * 0.2
                end
                p.glow.BackgroundTransparency = glowIntensity
                
                local shimmer = math.sin(tick() * 4 + p.pulsePhase) * 0.2 + 0.3
                p.highlight.BackgroundTransparency = shimmer
                
                p.vx = p.vx * 0.995
                p.vy = p.vy * 0.998
                
                p.frame.Position = UDim2.new(newX, 0, newY, 0)
            end
        end
    end
end

-- ========================================
-- VISUAL EFFECTS
-- ========================================

local function CreateButtonGlow(button, hoverColor, originalColor)
    local glowBorder = Instance.new("Frame")
    glowBorder.Size = UDim2.new(1, 8, 1, 8)
    glowBorder.Position = UDim2.new(0, -4, 0, -4)
    glowBorder.BackgroundTransparency = 1
    glowBorder.ZIndex = button.ZIndex - 1
    glowBorder.Visible = false
    glowBorder.Selectable = false
    glowBorder.Parent = button
    
    local corner = Instance.new("UICorner")
    corner.CornerRadius = UDim.new(0, 14)
    corner.Parent = glowBorder
    
    local stroke = Instance.new("UIStroke")
    stroke.Color = Colors.NeonWhite
    stroke.Thickness = 2
    stroke.Transparency = 0.3
    stroke.Parent = glowBorder
    
    local gradient = Instance.new("UIGradient")
    gradient.Color = ColorSequence.new{
        ColorSequenceKeypoint.new(0, Colors.NeonWhite),
        ColorSequenceKeypoint.new(0.5, Colors.NeonGlow),
        ColorSequenceKeypoint.new(1, Colors.NeonWhite)
    }
    gradient.Transparency = NumberSequence.new{
        NumberSequenceKeypoint.new(0, 0.8),
        NumberSequenceKeypoint.new(0.2, 0.1),
        NumberSequenceKeypoint.new(0.8, 0.1),
        NumberSequenceKeypoint.new(1, 0.8)
    }
    gradient.Parent = stroke
    
    local currentTween = nil
    local buttonId = tostring(button)
    
    button.MouseEnter:Connect(function()
        State.FocusStates.ButtonHovered[buttonId] = true
        glowBorder.Visible = true
        
        Services.TweenService:Create(button, TweenInfo.new(0.2, Enum.EasingStyle.Quad), 
            {BackgroundColor3 = hoverColor}):Play()
        
        Services.TweenService:Create(stroke, TweenInfo.new(0.2, Enum.EasingStyle.Quad), 
            {Transparency = 0.1}):Play()
        
        if currentTween then currentTween:Cancel() end
        currentTween = Services.TweenService:Create(gradient, 
            TweenInfo.new(1.5, Enum.EasingStyle.Linear, Enum.EasingDirection.InOut, -1), 
            {Rotation = 360})
        currentTween:Play()
    end)
    
    button.MouseLeave:Connect(function()
        State.FocusStates.ButtonHovered[buttonId] = false
        
        Services.TweenService:Create(button, TweenInfo.new(0.2, Enum.EasingStyle.Quad), 
            {BackgroundColor3 = originalColor}):Play()
        
        Services.TweenService:Create(stroke, TweenInfo.new(0.3, Enum.EasingStyle.Quad), 
            {Transparency = 0.8}):Play()
        
        if currentTween then
            currentTween:Cancel()
            gradient.Rotation = 0
        end
        
        task.spawn(function()
            task.wait(0.3)
            if glowBorder and glowBorder.Parent then
                glowBorder.Visible = false
            end
        end)
    end)
    
    return {glowBorder, stroke, gradient}
end

-- ========================================
-- STATUS & FEEDBACK FUNCTIONS
-- ========================================

local function ShowStatus(message, isError, isSuccess)
    if not UI.Status then return end
    
    UI.Status.Text = message
    if isSuccess then
        UI.Status.TextColor3 = Colors.Success
    elseif isError then
        UI.Status.TextColor3 = Colors.Error
    else
        UI.Status.TextColor3 = Colors.Warning
    end
    
    UI.Status.TextTransparency = 1
    Services.TweenService:Create(UI.Status, TweenInfo.new(0.3, Enum.EasingStyle.Quad), 
        {TextTransparency = 0}):Play()
end

local function ClearStatus()
    if UI.Status then
        Services.TweenService:Create(UI.Status, TweenInfo.new(0.3, Enum.EasingStyle.Quad), 
            {TextTransparency = 1}):Play()
    end
end

local function SetLoading(isLoading)
    State.IsLoading = isLoading
    if not UI.Buttons then return end
    
    UI.Buttons.Loading.Container.Visible = isLoading
    UI.Buttons.Submit.Text = isLoading and "" or "Verify Access Key"
    
    if isLoading then
        local tween = Services.TweenService:Create(UI.Buttons.Loading.Spinner, 
            TweenInfo.new(1, Enum.EasingStyle.Linear, Enum.EasingDirection.InOut, -1), 
            {Rotation = 360})
        tween:Play()
        State.Animations.SpinTween = tween
    else
        if State.Animations.SpinTween then
            State.Animations.SpinTween:Cancel()
            UI.Buttons.Loading.Spinner.Rotation = 0
        end
    end
end

-- ========================================
-- GUI TEARDOWN + HANDOFF PARA O LOADER
-- ========================================

local function DestroyGUI()
    if State.IsDestroyed then return end
    State.IsDestroyed = true
    pcall(function()
        if UI.ScreenGui and UI.ScreenGui.Parent then
            UI.ScreenGui:Destroy()
        end
    end)
end

-- Chamado quando a key e aceita (validada ou vinda do cache)
local function OnKeyAccepted(key)
    KeyCache.Save(key)
    DestroyGUI()

    -- Entrega a key para o loader da Syntrix (igual ao exemplo oficial)
    pcall(function()
        if type(_SxSetKey) == "function" then
            _SxSetKey(key)
        end
    end)

    -- OPCIONAL: rode o loadstring do seu script aqui depois da key validada, ex.:
    -- loadstring(game:HttpGet("https://loadstrings.syntrx.xyz/l/SEU_SCRIPT_ID"))()
end

-- ========================================
-- INPUT HANDLING
-- ========================================

local function UpdateCharCounter()
    if not UI.Input then return end
    
    local currentLength = string.len(UI.Input.TextBox.Text)
    UI.Input.Counter.Text = currentLength .. "/" .. Config.MaxKeyLength
    
    if currentLength >= Config.MaxKeyLength then
        UI.Input.Counter.TextColor3 = Colors.Error
    elseif currentLength >= Config.MaxKeyLength * 0.8 then
        UI.Input.Counter.TextColor3 = Colors.Warning
    else
        UI.Input.Counter.TextColor3 = Colors.TextSecondary
    end
end

local function CopyToClipboard(text, successMessage)
    local success = pcall(function()
        if setclipboard then
            setclipboard(text)
            ShowStatus(successMessage, false, true)
        else
            ShowStatus("Link: " .. text, false, true)
        end
    end)
    
    if not success then
        ShowStatus("Link: " .. text, false, true)
    end
end

-- Validacao real via API Syntrix (botao Verify + tecla Enter usam isto)
local function SubmitKey()
    if State.IsLoading or State.IsDestroyed then return end

    local key = UI.Input.TextBox.Text
    key = key:match("^%s*(.-)%s*$") -- trim de espacos

    if key == "" then
        ShowStatus("Please enter an access key", true)
        UI.Input.TextBox:CaptureFocus()
        return
    end

    SetLoading(true)
    ShowStatus("Validating key...", false, false)

    task.spawn(function()
        local result = KeySystem.Validate(key)
        if State.IsDestroyed then return end

        SetLoading(false)

        if result.success then
            ShowStatus("Key validated! Loading...", false, true)
            task.wait(0.6)
            OnKeyAccepted(key)
        else
            ShowStatus(result.error or "Invalid key, try again", true)
        end
    end)
end

-- ========================================
-- EVENT CONNECTIONS
-- ========================================

local function ConnectEvents()
    Services.UserInputService.InputChanged:Connect(function(input, gameProcessed)
        if input.UserInputType == Enum.UserInputType.MouseMovement then
            State.MousePosition.X = input.Position.X
            State.MousePosition.Y = input.Position.Y
        end
    end)
    
    UI.Input.TextBox:GetPropertyChangedSignal("Text"):Connect(function()
        local currentText = UI.Input.TextBox.Text
        
        if string.len(currentText) > Config.MaxKeyLength then
            UI.Input.TextBox.Text = string.sub(currentText, 1, Config.MaxKeyLength)
            ShowStatus("Maximum character limit reached (" .. Config.MaxKeyLength .. ")", true)
        end
        
        UpdateCharCounter()
        ClearStatus()
    end)
    
    local inputGlowTween = nil
    
    UI.Input.TextBox.Focused:Connect(function()
        State.FocusStates.InputFocused = true
        UI.Input.Glow.Frame.Visible = true
        
        Services.TweenService:Create(UI.Input.Stroke, 
            TweenInfo.new(0.2, Enum.EasingStyle.Quad), 
            {Color = Colors.NeonWhite, Transparency = 0.1}):Play()
        
        Services.TweenService:Create(UI.Input.Glow.Stroke, 
            TweenInfo.new(0.2, Enum.EasingStyle.Quad), 
            {Transparency = 0.1}):Play()
        
        if inputGlowTween then inputGlowTween:Cancel() end
        inputGlowTween = Services.TweenService:Create(UI.Input.Glow.Gradient, 
            TweenInfo.new(2, Enum.EasingStyle.Linear, Enum.EasingDirection.InOut, -1), 
            {Rotation = 360})
        inputGlowTween:Play()
        State.Animations.InputGlowTween = inputGlowTween
        ClearStatus()
    end)
    
    UI.Input.TextBox.FocusLost:Connect(function()
        State.FocusStates.InputFocused = false
        
        Services.TweenService:Create(UI.Input.Stroke, 
            TweenInfo.new(0.2, Enum.EasingStyle.Quad), 
            {Color = Colors.Border, Transparency = 0.3}):Play()
        
        Services.TweenService:Create(UI.Input.Glow.Stroke, 
            TweenInfo.new(0.3, Enum.EasingStyle.Quad), 
            {Transparency = 0.8}):Play()
        
        if inputGlowTween then
            inputGlowTween:Cancel()
            UI.Input.Glow.Gradient.Rotation = 0
            State.Animations.InputGlowTween = nil
        end
        
        task.spawn(function()
            task.wait(0.3)
            if UI.Input.Glow.Frame and UI.Input.Glow.Frame.Parent then
                UI.Input.Glow.Frame.Visible = false
            end
        end)
    end)
    
    Services.UserInputService.InputBegan:Connect(function(input, gameProcessed)
        if gameProcessed or State.IsDestroyed then return end
        
        if input.KeyCode == Enum.KeyCode.Return and UI.Input.TextBox:IsFocused() then
            SubmitKey()
        end
    end)
    
    UI.Buttons.Submit.MouseButton1Click:Connect(function()
        SubmitKey()
    end)
    
    UI.Buttons.GetKey.MouseButton1Click:Connect(function()
        CopyToClipboard(Syntrix.GetKeyUrl, "Get Key link copied to clipboard!")
        pcall(function()
            if type(openurl) == "function" then openurl(Syntrix.GetKeyUrl) end
        end)
    end)
    
    UI.Buttons.Discord.MouseButton1Click:Connect(function()
        if type(Syntrix.DiscordInvite) ~= "string" or Syntrix.DiscordInvite == "" or Syntrix.DiscordInvite:find("your%-invite") then
            ShowStatus("Set your Discord invite in Syntrix.DiscordInvite", true)
            return
        end
        CopyToClipboard(Syntrix.DiscordInvite, "Discord invite copied to clipboard!")
        pcall(function()
            if type(openurl) == "function" then openurl(Syntrix.DiscordInvite) end
        end)
    end)
end

-- ========================================
-- ANIMATION LOOPS
-- ========================================

local function StartAnimationLoops()
    State.Animations.BorderTween = nil
    State.Animations.IconTween = nil
    State.FocusStates.AnimationsActive = true
    
    task.spawn(function()
        for i = 1, 25 do
            if State.IsDestroyed then break end
            CreateParticle()
            task.wait(math.random(20, 100) / 1000)
        end
        
        while not State.IsDestroyed and UI.ScreenGui.Parent do
            if #State.Particles < Config.ParticleCount then
                CreateParticle()
            end
            task.wait(math.random(400, 1200) / 1000)
        end
    end)
    
    task.spawn(function()
        while not State.IsDestroyed and UI.ScreenGui.Parent do
            pcall(UpdateParticles)
            task.wait(1/Config.ParticleSpeed)
        end
    end)
    
    task.spawn(function()
        while not State.IsDestroyed and UI.AnimatedBorder and UI.AnimatedBorder.Frame.Parent do
            if State.Animations.BorderTween then
                State.Animations.BorderTween:Cancel()
            end
            
            local startRotation = UI.AnimatedBorder.Gradient.Rotation
            local tween = Services.TweenService:Create(UI.AnimatedBorder.Gradient, 
                TweenInfo.new(4, Enum.EasingStyle.Linear), 
                {Rotation = startRotation + 360})

            State.Animations.BorderTween = tween
            tween:Play()

            local success = pcall(function()
                tween.Completed:Wait()
            end)
            
            if not success then
                task.wait(4)
            end
            
            if UI.AnimatedBorder and UI.AnimatedBorder.Gradient and UI.AnimatedBorder.Gradient.Parent then
                UI.AnimatedBorder.Gradient.Rotation = UI.AnimatedBorder.Gradient.Rotation % 360
            end
            
            task.wait(0.1)
        end
    end)
    
    task.spawn(function()
        while not State.IsDestroyed and UI.Header and UI.Header.IconGlow and UI.Header.IconGlow.Parent do
            if State.Animations.IconTween then
                State.Animations.IconTween:Cancel()
            end
            
            local startRotation = UI.Header.IconGlow.Rotation
            State.Animations.IconTween = Services.TweenService:Create(UI.Header.IconGlow, 
                TweenInfo.new(3, Enum.EasingStyle.Linear), 
                {Rotation = startRotation + 360})
            
            State.Animations.IconTween:Play()

            local success = pcall(function()
                State.Animations.IconTween.Completed:Wait()
            end)
            
            if not success then
                task.wait(3)
            end
            
            if UI.Header and UI.Header.IconGlow and UI.Header.IconGlow.Parent then
                UI.Header.IconGlow.Rotation = UI.Header.IconGlow.Rotation % 360
            end
            
            task.wait(0.1)
        end
    end)
end

-- ========================================
-- ENTRANCE ANIMATION
-- ========================================

local function PlayEntranceAnimation()
    UI.Container.Size = UDim2.new(0, 0, 0, 0)
    UI.Container.BackgroundTransparency = 1
    UI.Backdrop.BackgroundTransparency = 1
    
    Services.TweenService:Create(UI.Backdrop, 
        TweenInfo.new(0.3, Enum.EasingStyle.Quad), 
        {BackgroundTransparency = 0.1}):Play()
    
    task.wait(0.1)
    
    Services.TweenService:Create(UI.Container, 
        TweenInfo.new(0.4, Enum.EasingStyle.Back, Enum.EasingDirection.Out), 
        {Size = UDim2.new(0, 420, 0, 600), BackgroundTransparency = 0}):Play()
    
    task.wait(0.5)
    UI.Input.TextBox:CaptureFocus()
end

-- ========================================
-- MAIN INITIALIZATION
-- ========================================

local function Initialize()
    local screenGui = CreateMainGUI()
    local backdrop = CreateBackdrop(screenGui)
    CreateParticleContainer(backdrop)
    local container = CreateContainer(screenGui)
    CreateAnimatedBorder(container)
    CreateHeader(container)
    local content = CreateContent(container)
    CreateInputSection(content)
    CreateButtons(content)
    CreateStatus(content)
    
    CreateButtonGlow(UI.Buttons.Submit, Colors.HoverPrimary, Colors.Primary)
    CreateButtonGlow(UI.Buttons.GetKey, Colors.HoverGetKey, Colors.GetKey)
    CreateButtonGlow(UI.Buttons.Discord, Colors.HoverDiscord, Colors.Discord)
    
    UpdateCharCounter()
    ConnectEvents()
    StartAnimationLoops()
    PlayEntranceAnimation()
end

-- ========================================
-- AUTO-LOGIN (KEY EM CACHE) + START
-- ========================================

-- Tenta validar uma key salva antes mesmo de mostrar a GUI
local function TryAutoLogin()
    if not Syntrix.CacheKey then return false end

    local cachedKey = KeyCache.Load()
    if not cachedKey then return false end

    local result = KeySystem.Validate(cachedKey)
    if result.success then
        OnKeyAccepted(cachedKey)
        return true
    end

    -- key do cache expirou/invalidou: limpa e mostra a GUI
    KeyCache.Clear()
    return false
end

if not TryAutoLogin() then
    Initialize()
end
