--[[
                            YUTA FOX HUB PROJECT
            This was made by YUtaFox Team
            KEYSYSTEM UI built with Local Validation
            Copyright © 2022-2026 YUtaFox Team - All Rights Reserved.
]]--

-- ============================================================
-- CONFIGURAÇÕES DO SISTEMA DE CHAVES (VALIDAÇÃO LOCAL)
-- ============================================================
local KEY_CONFIG = {
    -- Lista de chaves válidas (adicione quantas quiser)
    VALID_KEYS = {
        "YUtaFox-1234",
        "YUtaFox-5678",
        "FREE-2024",
        "TEST-KEY",
        -- Adicione mais chaves aqui
    },
    -- Pasta onde salvar a chave usada
    FOLDER = "YUtaFox Hub",
    KEY_FILE = "YUtaFox Hub/Key.json"
}

local API_CONFIG = {
    BASE_URL = "https://api.yutafox.cc",
    FALLBACK_URL = "http://165.232.169.51:22527",
    DISCORD_INVITE = "https://discord.gg/yutafox",
}

local Directory = "https://raw.githubusercontent.com/flazhy/QuantumOnyx/refs/heads/main/Games"
local Scripts = {
    Free = {
        [994732206] = Directory .. "/BloxFruits.lua",
        [9186719164] = Directory .. "/SailorPiece.lua",
        [8191429227] = Directory .. "/CutTrees.lua",
    },
}

local FOLDER = KEY_CONFIG.FOLDER
local KEY_FILE = KEY_CONFIG.KEY_FILE

local Players = game:GetService("Players")
local HttpService = game:GetService("HttpService")
local TweenService = game:GetService("TweenService")
local UserInputService = game:GetService("UserInputService")
local LocalPlayer = Players.LocalPlayer
local Mouse = LocalPlayer:GetMouse()
local GameId = game.GameId
local gameId = GameId

local HttpRequest = (syn and syn.request)
    or (http and http.request)
    or http_request
    or request
    or (fluxus and fluxus.request)
    or (delta and delta.request)

local IsFileFunc = isfile or function(file) return false end
local ReadFileFunc = readfile or function(file) return "" end
local WriteFileFunc = writefile or function(file, content) end
local MakeFolderFunc = makefolder or function(folder) end
local IsFolderFunc = isfolder or function(folder) return false end

local function GetExecutorName()
    if identifyexecutor then return identifyexecutor() end
    if syn then return "Synapse X" end
    if KRNL_LOADED then return "Krnl" end
    if fluxus then return "Fluxus" end
    if is_sirhurt_closure then return "SirHurt" end
    return "Unknown Executor"
end

local function GetHWID()
    local hwid = nil
    if gethwid then
        pcall(function() hwid = gethwid() end)
    elseif get_hwid then
        pcall(function() hwid = get_hwid() end)
    end
    if not hwid or tostring(hwid) == "" then
        pcall(function()
            hwid = game:GetService("RbxAnalyticsService"):GetClientId()
        end)
    end
    if not hwid or tostring(hwid) == "" then
        hwid = "FALLBACK-" .. tostring(LocalPlayer.UserId) .. "-" .. tostring(game.PlaceId)
    end
    return tostring(hwid)
end

local function Tween(obj, props, t, style, dir)
    style = style or Enum.EasingStyle.Quint
    dir = dir or Enum.EasingDirection.Out
    TweenService:Create(obj, TweenInfo.new(t, style, dir), props):Play()
end

local function Protect(gui)
    local env = (getgenv and getgenv()) or _G
    if env.HIDEUI then
        gui.Parent = env.HIDEUI
    elseif gethui then
        gui.Parent = gethui()
    elseif syn and syn.protect_gui then
        syn.protect_gui(gui)
        gui.Parent = game:GetService("CoreGui")
    else
        gui.Parent = game:GetService("CoreGui")
    end
end

local function New(class, props, parent)
    local inst = Instance.new(class)
    for k, v in pairs(props) do
        if k ~= "Children" and k ~= "Parent" then
            pcall(function() inst[k] = v end)
        end
    end
    if props.Children then
        for _, c in ipairs(props.Children) do
            pcall(function() c.Parent = inst end)
        end
    end
    inst.Parent = props.Parent or parent
    return inst
end

local function CircleRipple(btn, mx, my)
    task.spawn(function()
        btn.ClipsDescendants = true
        local nx = mx - btn.AbsolutePosition.X
        local ny = my - btn.AbsolutePosition.Y
        local sz = math.max(btn.AbsoluteSize.X, btn.AbsoluteSize.Y) * 1.6
        local c = New("ImageLabel", {
            Name = "Ripple",
            Image = "rbxassetid://266543268",
            ImageColor3 = Color3.fromRGB(255, 255, 255),
            ImageTransparency = 0.82,
            BackgroundTransparency = 1,
            ZIndex = btn.ZIndex + 5,
            Size = UDim2.new(0, 0, 0, 0),
            Position = UDim2.new(0, nx, 0, ny),
        }, btn)
        Tween(c, { Size = UDim2.new(0, sz, 0, sz), Position = UDim2.new(0.5, -sz/2, 0.5, -sz/2) }, 0.45, Enum.EasingStyle.Quad)
        Tween(c, { ImageTransparency = 1 }, 0.45, Enum.EasingStyle.Linear)
        task.wait(0.46)
        c:Destroy()
    end)
end

local StarterGui = game:GetService("StarterGui")
local function Notify(title, desc, accent, duration)
    pcall(function()
        StarterGui:SetCore("SendNotification", {
            Title = title or "YUtaFox",
            Text = desc or "",
            Duration = duration or 5,
        })
    end)
end

local function ToTime(expire)
    if not expire or expire <= 0 then return "Lifetime" end
    local left = expire - os.time()
    if left < 0 then return "Expired" end
    local days = math.floor(left / 86400)
    local hours = math.floor((left % 86400) / 3600)
    local minutes = math.floor((left % 3600) / 60)
    if days > 0 then return string.format("%dd %dh", days, hours) end
    if hours > 0 then return string.format("%dh %dm", hours, minutes) end
    return string.format("%dm", minutes)
end

-- ============================================================
-- SISTEMA DE CHAVES LOCAL (SEM KEYAUTH)
-- ============================================================
local KeySystem = {}

function KeySystem:IsValidKey(key)
    if not key or key == "" then return false end
    key = tostring(key):gsub("%s+", "")
    for _, validKey in ipairs(KEY_CONFIG.VALID_KEYS) do
        if key == validKey then
            return true
        end
    end
    return false
end

function KeySystem:SaveKey(key)
    pcall(function()
        if not IsFolderFunc(FOLDER) then MakeFolderFunc(FOLDER) end
        local saveData = {
            key = tostring(key):gsub("%s+", ""),
            hwid = GetHWID(),
            date = os.time()
        }
        WriteFileFunc(KEY_FILE, HttpService:JSONEncode(saveData))
    end)
end

function KeySystem:LoadSavedKey()
    if IsFolderFunc(FOLDER) and IsFileFunc(KEY_FILE) then
        local ok, v = pcall(function()
            return HttpService:JSONDecode(ReadFileFunc(KEY_FILE))
        end)
        if ok and type(v) == "table" and v.key then return v.key
    end
    return nil
end

function KeySystem:ClearKey()
    pcall(function()
        if IsFolderFunc(FOLDER) then WriteFileFunc(KEY_FILE, HttpService:JSONEncode({})) end
    end)
end

function KeySystem:CheckHWID()
    -- Verificação simples de HWID (pode ser expandida)
    return true
end

local KeySystemInstance = KeySystem

local function LoadScript(tier, scriptPayload)
    if tier == "Free" then
        local url = Scripts.Free[GameId]
        if url then
            pcall(function() loadstring(game:HttpGet(url))() end)
        else
            warn("[YUtaFox] No free script found for GameId: " .. tostring(GameId))
        end
    elseif tier == "Premium" then
        if scriptPayload and type(scriptPayload) == "string" and #scriptPayload > 100 then
            task.spawn(function()
                local func, compileErr = loadstring(scriptPayload)
                if func then
                    local success, runtimeErr = pcall(func)
                    if not success then
                        warn("[YUtaFox BloxFruits] Runtime error: " .. tostring(runtimeErr))
                        Notify("Script Runtime Error", tostring(runtimeErr):sub(1, 80), Color3.fromRGB(255, 90, 110), 10)
                    end
                else
                    warn("[YUtaFox BloxFruits] Compilation error: " .. tostring(compileErr))
                    Notify("Script Compile Error", tostring(compileErr):sub(1, 80), Color3.fromRGB(255, 90, 110), 10)
                end
            end)
        else
            warn("[YUtaFox BloxFruits] No payload, loading free fallback...")
            LoadScript("Free", nil)
        end
    end
end

-- ============================================================
-- TELA DE CHAVE (UI) - VERSÃO LOCAL
-- ============================================================
local function ShowKeyUI()
    local done = false
    local isPremium = false
    local submitting = false

    local supportInfo = {
        { label = "Discord", value = "discord.gg/yutafox" },
        { label = "Game", value = (pcall(function() return game:GetService("MarketplaceService"):GetProductInfo(game.PlaceId).Name end) and game:GetService("MarketplaceService"):GetProductInfo(game.PlaceId).Name) or "Unknown" },
        { label = "Version", value = "v.Freemium" },
    }

    local SG = Instance.new("ScreenGui")
    SG.Name = "YF_" .. tostring(math.random(1e6))
    SG.ZIndexBehavior = Enum.ZIndexBehavior.Global
    SG.ResetOnSpawn = false
    SG.IgnoreGuiInset = true
    Protect(SG)

    local Backdrop = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(0, 0, 0),
        BackgroundTransparency = 0.45,
        BorderSizePixel = 0,
        Size = UDim2.new(1, 0, 1, 0),
        ZIndex = 200,
        Parent = SG,
    })

    local W, H = 450, 310
    local Card = New("Frame", {
        AnchorPoint = Vector2.new(0.5, 0.5),
        Position = UDim2.new(0.5, 0, 0.5, 0),
        Size = UDim2.new(0, W, 0, H),
        BackgroundColor3 = Color3.fromRGB(15, 12, 24),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        ZIndex = 201,
        ClipsDescendants = true,
        Parent = SG,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 14) }),
            New("UIStroke", {
                Color = Color3.fromRGB(120, 60, 220),
                Transparency = 0.3,
                Thickness = 1.5,
                ApplyStrokeMode = Enum.ApplyStrokeMode.Border,
            }),
        }
    })

    New("Frame", {
        BackgroundColor3 = Color3.fromRGB(80, 20, 160),
        BackgroundTransparency = 0.85,
        BorderSizePixel = 0,
        Position = UDim2.new(0, -60, 0, -60),
        Size = UDim2.new(0, 220, 0, 220),
        ZIndex = 201,
        Parent = Card,
        Children = { New("UICorner", { CornerRadius = UDim.new(1, 0) }) }
    })

    New("Frame", {
        BackgroundColor3 = Color3.fromRGB(40, 10, 110),
        BackgroundTransparency = 0.85,
        BorderSizePixel = 0,
        Position = UDim2.new(1, -100, 1, -100),
        Size = UDim2.new(0, 180, 0, 180),
        ZIndex = 201,
        Parent = Card,
        Children = { New("UICorner", { CornerRadius = UDim.new(1, 0) }) }
    })

    local Header = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(22, 16, 36),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Size = UDim2.new(1, 0, 0, 44),
        ZIndex = 202,
        Parent = Card,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 14) }),
            New("Frame", {
                BackgroundColor3 = Color3.fromRGB(22, 16, 36),
                BackgroundTransparency = 0,
                BorderSizePixel = 0,
                Position = UDim2.new(0, 0, 0.5, 0),
                Size = UDim2.new(1, 0, 0.5, 0),
                ZIndex = 202
            }),
        }
    })

    New("ImageLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 13, 0.5, -8),
        Size = UDim2.new(0, 16, 0, 16),
        Image = "rbxassetid://7733992528",
        ImageColor3 = Color3.fromRGB(155, 90, 255),
        ZIndex = 203,
        Parent = Header
    })

    New("TextLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 35, 0, 0),
        Size = UDim2.new(1, -130, 1, 0),
        Font = Enum.Font.FredokaOne,
        Text = "YUtaFox — Key System",
        TextColor3 = Color3.fromRGB(220, 200, 255),
        TextSize = 14,
        TextXAlignment = Enum.TextXAlignment.Left,
        ZIndex = 203,
        Parent = Header
    })

    New("Frame", {
        AnchorPoint = Vector2.new(1, 0.5),
        BackgroundColor3 = Color3.fromRGB(30, 60, 20),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Position = UDim2.new(1, -40, 0.5, 0),
        Size = UDim2.new(0, 72, 0, 20),
        ZIndex = 203,
        Parent = Header,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 5) }),
            New("UIStroke", { Color = Color3.fromRGB(80, 200, 110), Transparency = 0.3, Thickness = 1, ApplyStrokeMode = Enum.ApplyStrokeMode.Border }),
            New("TextLabel", {
                BackgroundTransparency = 1,
                Size = UDim2.new(1, 0, 1, 0),
                Font = Enum.Font.GothamBold,
                Text = "Freemium",
                TextColor3 = Color3.fromRGB(130, 235, 160),
                TextSize = 10,
                TextXAlignment = Enum.TextXAlignment.Center,
                ZIndex = 204
            }),
        }
    })

    local CloseBtn = New("ImageButton", {
        BackgroundTransparency = 1,
        AnchorPoint = Vector2.new(1, 0.5),
        Position = UDim2.new(1, -8, 0.5, 0),
        Size = UDim2.new(0, 20, 0, 20),
        Image = "rbxassetid://79324227570635",
        ImageColor3 = Color3.fromRGB(200, 80, 80),
        ZIndex = 203,
        Parent = Header
    })

    CloseBtn.MouseButton1Click:Connect(function()
        SG:Destroy()
    end)

    New("Frame", {
        BackgroundColor3 = Color3.fromRGB(120, 60, 220),
        BackgroundTransparency = 0.3,
        BorderSizePixel = 0,
        Position = UDim2.new(0, 0, 0, 44),
        Size = UDim2.new(1, 0, 0, 1),
        ZIndex = 202,
        Parent = Card,
    })

    local LW = 180
    local RX = LW + 18
    local RW = W - RX - 10

    New("Frame", {
        BackgroundColor3 = Color3.fromRGB(100, 50, 200),
        BackgroundTransparency = 0.5,
        BorderSizePixel = 0,
        Position = UDim2.new(0, LW + 8, 0, 52),
        Size = UDim2.new(0, 1, 0, H - 60),
        ZIndex = 202,
        Parent = Card,
    })

    local InfoBox = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(22, 16, 36),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Position = UDim2.new(0, 8, 0, 52),
        Size = UDim2.new(0, LW, 0, 112),
        ZIndex = 202,
        Parent = Card,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 8) }),
            New("UIStroke", { Color = Color3.fromRGB(100, 50, 190), Transparency = 0.4, Thickness = 1, ApplyStrokeMode = Enum.ApplyStrokeMode.Border }),
        }
    })

    New("TextLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 9, 0, 5),
        Size = UDim2.new(1, -14, 0, 13),
        Font = Enum.Font.GothamBold,
        Text = "Information",
        TextColor3 = Color3.fromRGB(160, 110, 240),
        TextSize = 9,
        TextXAlignment = Enum.TextXAlignment.Left,
        ZIndex = 203,
        Parent = InfoBox
    })

    New("Frame", {
        BackgroundColor3 = Color3.fromRGB(110, 60, 200),
        BackgroundTransparency = 0.6,
        BorderSizePixel = 0,
        Position = UDim2.new(0, 7, 0, 20),
        Size = UDim2.new(1, -14, 0, 1),
        ZIndex = 203,
        Parent = InfoBox,
    })

    local rowY = 26
    for _, info in ipairs(supportInfo) do
        New("TextLabel", {
            BackgroundTransparency = 1,
            Position = UDim2.new(0, 9, 0, rowY),
            Size = UDim2.new(0, 55, 0, 12),
            Font = Enum.Font.GothamBold,
            Text = (info.label or "") .. ":",
            TextColor3 = Color3.fromRGB(140, 110, 190),
            TextSize = 9,
            TextXAlignment = Enum.TextXAlignment.Left,
            ZIndex = 203,
            Parent = InfoBox
        })
        New("TextLabel", {
            BackgroundTransparency = 1,
            Position = UDim2.new(0, 64, 0, rowY),
            Size = UDim2.new(1, -70, 0, 12),
            Font = Enum.Font.Gotham,
            Text = tostring(info.value or ""),
            TextColor3 = Color3.fromRGB(200, 180, 240),
            TextSize = 9,
            TextXAlignment = Enum.TextXAlignment.Left,
            TextTruncate = Enum.TextTruncate.AtEnd,
            ZIndex = 203,
            Parent = InfoBox
        })
        rowY = rowY + 16
        if rowY > 96 then break end
    end

    local ProfileBox = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(22, 16, 36),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Position = UDim2.new(0, 8, 0, 170),
        Size = UDim2.new(0, LW, 0, 128),
        ZIndex = 202,
        Parent = Card,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 8) }),
            New("UIStroke", { Color = Color3.fromRGB(100, 50, 190), Transparency = 0.4, Thickness = 1, ApplyStrokeMode = Enum.ApplyStrokeMode.Border }),
        }
    })

    New("TextLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 9, 0, 5),
        Size = UDim2.new(1, -14, 0, 13),
        Font = Enum.Font.GothamBold,
        Text = "User Profile",
        TextColor3 = Color3.fromRGB(160, 110, 240),
        TextSize = 9,
        TextXAlignment = Enum.TextXAlignment.Left,
        ZIndex = 203,
        Parent = ProfileBox
    })

    New("Frame", {
        BackgroundColor3 = Color3.fromRGB(110, 60, 200),
        BackgroundTransparency = 0.6,
        BorderSizePixel = 0,
        Position = UDim2.new(0, 7, 0, 20),
        Size = UDim2.new(1, -14, 0, 1),
        ZIndex = 203,
        Parent = ProfileBox,
    })

    local AvatarRing = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(110, 55, 210),
        BackgroundTransparency = 0.3,
        BorderSizePixel = 0,
        AnchorPoint = Vector2.new(0.5, 0),
        Position = UDim2.new(0.5, 0, 0, 28),
        Size = UDim2.new(0, 52, 0, 52),
        ZIndex = 203,
        Parent = ProfileBox,
        Children = { New("UICorner", { CornerRadius = UDim.new(1, 0) }) }
    })

    local AvatarImg = New("ImageLabel", {
        BackgroundColor3 = Color3.fromRGB(30, 15, 55),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        AnchorPoint = Vector2.new(0.5, 0.5),
        Position = UDim2.new(0.5, 0, 0.5, 0),
        Size = UDim2.new(0, 46, 0, 46),
        Image = "",
        ZIndex = 204,
        Parent = AvatarRing,
        Children = { New("UICorner", { CornerRadius = UDim.new(1, 0) }) }
    })

    local DisplayNameLbl = New("TextLabel", {
        BackgroundTransparency = 1,
        AnchorPoint = Vector2.new(0.5, 0),
        Position = UDim2.new(0.5, 0, 0, 86),
        Size = UDim2.new(1, -12, 0, 14),
        Font = Enum.Font.GothamBold,
        Text = LocalPlayer.DisplayName,
        TextColor3 = Color3.fromRGB(220, 205, 255),
        TextSize = 11,
        TextXAlignment = Enum.TextXAlignment.Center,
        TextTruncate = Enum.TextTruncate.AtEnd,
        ZIndex = 203,
        Parent = ProfileBox
    })

    New("TextLabel", {
        BackgroundTransparency = 1,
        AnchorPoint = Vector2.new(0.5, 0),
        Position = UDim2.new(0.5, 0, 0, 102),
        Size = UDim2.new(1, -12, 0, 12),
        Font = Enum.Font.Gotham,
        Text = "@" .. LocalPlayer.Name,
        TextColor3 = Color3.fromRGB(145, 125, 185),
        TextSize = 9,
        TextXAlignment = Enum.TextXAlignment.Center,
        TextTruncate = Enum.TextTruncate.AtEnd,
        ZIndex = 203,
        Parent = ProfileBox
    })

    task.spawn(function()
        local ok, img = pcall(function()
            return game:GetService("Players"):GetUserThumbnailAsync(
                LocalPlayer.UserId,
                Enum.ThumbnailType.HeadShot,
                Enum.ThumbnailSize.Size100x100
            )
        end)
        if ok and img then AvatarImg.Image = img end
    end)

    local NoticeBg = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(22, 16, 36),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Position = UDim2.new(0, RX, 0, 52),
        Size = UDim2.new(0, RW, 0, 50),
        ZIndex = 202,
        Parent = Card,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 7) }),
            New("UIStroke", { Color = Color3.fromRGB(80, 200, 110), Transparency = 0.4, Thickness = 1, ApplyStrokeMode = Enum.ApplyStrokeMode.Border }),
            New("Frame", {
                BackgroundColor3 = Color3.fromRGB(80, 200, 110),
                BorderSizePixel = 0,
                Position = UDim2.new(0, 0, 0.5, -10),
                Size = UDim2.new(0, 3, 0, 20),
                ZIndex = 203,
                Children = { New("UICorner", { CornerRadius = UDim.new(1, 0) }) }
            })
        }
    })

    New("TextLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 12, 0, 0),
        Size = UDim2.new(1, -16, 1, 0),
        Font = Enum.Font.Gotham,
        TextColor3 = Color3.fromRGB(140, 230, 170),
        TextSize = 10,
        TextWrapped = true,
        TextXAlignment = Enum.TextXAlignment.Left,
        TextYAlignment = Enum.TextYAlignment.Center,
        ZIndex = 203,
        Text = "Freemium — key is optional.\nEnter a key to unlock premium features.",
        Parent = NoticeBg
    })

    local LRMBar = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(22, 16, 36),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Position = UDim2.new(0, RX, 0, 110),
        Size = UDim2.new(0, RW, 0, 28),
        ZIndex = 202,
        Parent = Card,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 6) }),
            New("UIStroke", { Color = Color3.fromRGB(100, 50, 190), Transparency = 0.4, Thickness = 1, ApplyStrokeMode = Enum.ApplyStrokeMode.Border }),
        }
    })

    New("ImageLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 8, 0.5, -6),
        Size = UDim2.new(0, 12, 0, 12),
        Image = "rbxassetid://7733992528",
        ImageColor3 = Color3.fromRGB(150, 95, 225),
        ZIndex = 203,
        Parent = LRMBar
    })

    local LRMStatusLabel = New("TextLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 25, 0, 0),
        Size = UDim2.new(1, -30, 1, 0),
        Font = Enum.Font.GothamBold,
        Text = "Ready for Authentication",
        TextColor3 = Color3.fromRGB(180, 160, 225),
        TextSize = 10,
        TextXAlignment = Enum.TextXAlignment.Left,
        ZIndex = 203,
        Parent = LRMBar
    })

    local InputBg = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(10, 8, 18),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Position = UDim2.new(0, RX, 0, 146),
        Size = UDim2.new(0, RW, 0, 34),
        ZIndex = 202,
        Parent = Card,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 7) }),
            New("UIStroke", { Color = Color3.fromRGB(120, 60, 220), Transparency = 0.3, Thickness = 1, ApplyStrokeMode = Enum.ApplyStrokeMode.Border }),
        }
    })

    New("ImageLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 10, 0.5, -7),
        Size = UDim2.new(0, 14, 0, 14),
        Image = "rbxassetid://7733992528",
        ImageColor3 = Color3.fromRGB(140, 90, 215),
        ZIndex = 203,
        Parent = InputBg
    })

    local KeyInput = New("TextBox", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, 30, 0, 0),
        Size = UDim2.new(1, -38, 1, 0),
        Font = Enum.Font.GothamBold,
        PlaceholderText = "Enter premium key...",
        PlaceholderColor3 = Color3.fromRGB(110, 85, 155),
        Text = "",
        TextColor3 = Color3.fromRGB(225, 205, 255),
        TextSize = 11,
        TextXAlignment = Enum.TextXAlignment.Left,
        ClearTextOnFocus = false,
        ZIndex = 203,
        Parent = InputBg
    })

    local StatusLabel = New("TextLabel", {
        BackgroundTransparency = 1,
        Position = UDim2.new(0, RX, 0, 185),
        Size = UDim2.new(0, RW, 0, 13),
        Font = Enum.Font.GothamBold,
        Text = "HWID: " .. GetHWID():sub(1, 12) .. "...",
        TextColor3 = Color3.fromRGB(175, 155, 210),
        TextSize = 9,
        TextXAlignment = Enum.TextXAlignment.Center,
        ZIndex = 202,
        Parent = Card
    })

    local function SetStatus(msg, col)
        StatusLabel.Text = msg
        StatusLabel.TextColor3 = col or Color3.fromRGB(175, 155, 210)
    end

    local function AnimateClose()
        Tween(Card, { Size = UDim2.new(0, W * 0.65, 0, H * 0.65), BackgroundTransparency = 1 }, 0.20, Enum.EasingStyle.Quint, Enum.EasingDirection.In)
        Tween(Backdrop, { BackgroundTransparency = 1 }, 0.20, Enum.EasingStyle.Quint)
        task.delay(0.22, function()
            SG:Destroy()
            done = true
        end)
    end

    local function SubmitKey(keyStr)
        if submitting then return end
        submitting = true

        SetStatus("Validating key...", Color3.fromRGB(175, 150, 255))

        -- Validação local
        local isValid = KeySystemInstance:IsValidKey(keyStr)

        if isValid then
            isPremium = true
            KeySystemInstance:SaveKey(keyStr)

            LRMStatusLabel.Text = "Premium Active"
            LRMStatusLabel.TextColor3 = Color3.fromRGB(80, 230, 130)
            DisplayNameLbl.TextColor3 = Color3.fromRGB(130, 220, 160)

            SetStatus("✅ Key verified! Loading...", Color3.fromRGB(80, 230, 130))
            Notify("Key Verified", "Welcome to YUtaFox Premium!", Color3.fromRGB(80, 230, 130))

            task.wait(0.5)
            AnimateClose()

            print("[YUtaFox] Premium ativado com sucesso!")

        else
            submitting = false
            SetStatus("❌ Invalid key! Try again.", Color3.fromRGB(255, 90, 110))
            Notify("Key Rejected", "The key you entered is invalid.", Color3.fromRGB(255, 90, 110))
        end
    end

    -- Carregar chave salva
    local saved = KeySystemInstance:LoadSavedKey()
    if saved and #saved > 0 then
        KeyInput.Text = saved
        task.spawn(function()
            task.wait(0.5)
            SubmitKey(saved)
        end)
    end

    local BtnY = 202
    local BtnH = 30
    local BtnGap = 6
    local BtnW = math.floor((RW - BtnGap * 2) / 3)

    local function MakeBtn(label, px, w, bg, tc, cb)
        local btn = New("TextButton", {
            BackgroundColor3 = bg,
            BackgroundTransparency = 0,
            BorderSizePixel = 0,
            Position = UDim2.new(0, px, 0, BtnY),
            Size = UDim2.new(0, w, 0, BtnH),
            AutoButtonColor = false,
            Text = "",
            ClipsDescendants = true,
            ZIndex = 202,
            Parent = Card,
            Children = {
                New("UICorner", { CornerRadius = UDim.new(0, 7) }),
                New("TextLabel", {
                    BackgroundTransparency = 1,
                    Size = UDim2.new(1, 0, 1, 0),
                    Font = Enum.Font.FredokaOne,
                    Text = label,
                    TextColor3 = tc,
                    TextSize = 13,
                    TextXAlignment = Enum.TextXAlignment.Center,
                    ZIndex = 203
                })
            }
        })
        btn.MouseEnter:Connect(function() Tween(btn, { BackgroundColor3 = bg:Lerp(Color3.fromRGB(255, 255, 255), 0.15) }, 0.12) end)
        btn.MouseLeave:Connect(function() Tween(btn, { BackgroundColor3 = bg }, 0.16) end)
        btn.MouseButton1Click:Connect(function()
            CircleRipple(btn, Mouse.X, Mouse.Y)
            cb()
        end)
        return btn
    end

    MakeBtn("Free Version", RX, BtnW, Color3.fromRGB(45, 20, 85), Color3.fromRGB(200, 165, 255), function()
        if not Scripts.Free[gameId] then
            SetStatus("No free version for this game.", Color3.fromRGB(255, 150, 80))
        else
            isPremium = false
            AnimateClose()
            LoadScript("Free", nil)
        end
    end)

    local panelOpen = false
    local OptionPanel = New("Frame", {
        BackgroundColor3 = Color3.fromRGB(15, 12, 24),
        BackgroundTransparency = 0,
        BorderSizePixel = 0,
        Position = UDim2.new(0, RX + BtnW + BtnGap, 0, BtnY - 78),
        Size = UDim2.new(0, BtnW, 0, 72),
        ZIndex = 215,
        Visible = false,
        ClipsDescendants = false,
        Parent = Card,
        Children = {
            New("UICorner", { CornerRadius = UDim.new(0, 7) }),
            New("UIStroke", {
                Color = Color3.fromRGB(120, 60, 220),
                Transparency = 0.3,
                Thickness = 1,
                ApplyStrokeMode = Enum.ApplyStrokeMode.Border
            }),
        }
    })

    local function MakeOptionBtn(label, yPos, link, statusMsg)
        local btn = New("TextButton", {
            BackgroundColor3 = Color3.fromRGB(40, 20, 80),
            BackgroundTransparency = 0,
            BorderSizePixel = 0,
            Position = UDim2.new(0, 4, 0, yPos),
            Size = UDim2.new(1, -8, 0, 30),
            AutoButtonColor = false,
            Text = label,
            Font = Enum.Font.GothamBold,
            TextColor3 = Color3.fromRGB(210, 185, 255),
            TextSize = 11,
            ZIndex = 216,
            Parent = OptionPanel,
            Children = { New("UICorner", { CornerRadius = UDim.new(0, 5) }) }
        })
        btn.MouseEnter:Connect(function() Tween(btn, { BackgroundColor3 = Color3.fromRGB(60, 30, 110) }, 0.10) end)
        btn.MouseLeave:Connect(function() Tween(btn, { BackgroundColor3 = Color3.fromRGB(40, 20, 80) }, 0.12) end)
        btn.MouseButton1Click:Connect(function()
            CircleRipple(btn, Mouse.X, Mouse.Y)
            pcall(function() (setclipboard or toclipboard)(link) end)
            SetStatus(statusMsg, Color3.fromRGB(105, 195, 255))
            task.delay(0.12, function()
                panelOpen = false
                OptionPanel.Visible = false
            end)
        end)
        return btn
    end

    MakeOptionBtn("Get Key", 4, "https://discord.gg/yutafox", "Join our Discord!")
    MakeOptionBtn("Support", 38, "https://discord.gg/yutafox", "Join our Discord!")

    local getKeyBtn = MakeBtn("Options", RX + BtnW + BtnGap, BtnW, Color3.fromRGB(20, 45, 90), Color3.fromRGB(130, 195, 255), function()
        panelOpen = not panelOpen
        OptionPanel.Visible = panelOpen
    end)

    UserInputService.InputBegan:Connect(function(input, processed)
        if not panelOpen then return end
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
            local pos = input.Position
            local ap = OptionPanel.AbsolutePosition
            local as = OptionPanel.AbsoluteSize
            local onPanel = pos.X >= ap.X and pos.X <= ap.X + as.X and pos.Y >= ap.Y and pos.Y <= ap.Y + as.Y
            local gkp = getKeyBtn.AbsolutePosition
            local gks = getKeyBtn.AbsoluteSize
            local onBtn = pos.X >= gkp.X and pos.X <= gkp.X + gks.X and pos.Y >= gkp.Y and pos.Y <= gkp.Y + gks.Y
            if not onPanel and not onBtn then
                panelOpen = false
                OptionPanel.Visible = false
            end
        end
    end)

    MakeBtn("Enter Key", RX + (BtnW + BtnGap) * 2, BtnW, Color3.fromRGB(65, 25, 130), Color3.fromRGB(225, 180, 255), function()
        SubmitKey(KeyInput.Text)
    end)

    KeyInput.FocusLost:Connect(function(enterPressed)
        if enterPressed then
            SubmitKey(KeyInput.Text)
        end
    end)
end

-- ============================================================
-- INICIALIZAÇÃO
-- ============================================================
local function AuthenticateAndLoad()
    local saved = KeySystemInstance:LoadSavedKey()
    if saved and #saved > 0 then
        task.spawn(function()
            if KeySystemInstance:IsValidKey(saved) then
                Notify("Welcome Back", "Auto-logged in successfully!", Color3.fromRGB(80, 230, 130))
                print("[YUtaFox] Premium ativado via chave salva!")
            else
                KeySystemInstance:ClearKey()
                ShowKeyUI()
            end
        end)
    else
        ShowKeyUI()
    end
end

AuthenticateAndLoad()
