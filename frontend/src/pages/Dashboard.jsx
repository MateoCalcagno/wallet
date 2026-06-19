import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useDashboard } from '../hooks/useDashboard'
import { useTransactionHistory } from '../hooks/useTransactionHistory'
import { getIconConfig, getLabel, getAmountColor, getAmountPrefix, formatDate } from '../utils/transactionHelpers'
import NavItem from '../components/NavItem'
import NovaLogo from '../components/NovaLogo'

function Dashboard() {
  const { user, balance, isLoading, handleLogout } = useDashboard()
  const { history, page, setPage, totalPages, historyError } = useTransactionHistory()
  const [copiedField, setCopiedField] = useState(null)
  const [showBalance, setShowBalance] = useState(true)
  const navigate = useNavigate()

  const handleCopy = async (text, field) => {
    try {
      await navigator.clipboard.writeText(text)
      setCopiedField(field)
      setTimeout(() => setCopiedField(null), 2000)
    } catch {
      setCopiedField(null)
    }
  }

  if (isLoading) {
    return (
      <div className="flex min-h-screen">

        {/* Sidebar skeleton */}
        <div className="w-52 bg-slate-900 flex flex-col justify-between py-7 px-5 relative overflow-hidden shrink-0">
          <div className="absolute -top-14 -right-14 w-40 h-40 rounded-full bg-blue-900 opacity-35" />
          <div className="absolute -bottom-10 -left-10 w-28 h-28 rounded-full bg-blue-900 opacity-25" />
          <div className="z-10">
            <div className="mb-8"><NovaLogo /></div>
            <div className="flex flex-col gap-3">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="h-8 bg-slate-800 rounded-lg animate-pulse" />
              ))}
            </div>
          </div>
        </div>

        {/* Main skeleton */}
        <div className="flex-1 flex flex-col bg-gray-50">

          {/* Topbar skeleton */}
          <div className="bg-white border-b border-gray-100 px-6 py-4 flex justify-between items-center">
            <div className="h-4 w-28 bg-gray-200 rounded animate-pulse" />
            <div className="h-3 w-36 bg-gray-200 rounded animate-pulse" />
            <div className="w-8 h-8 rounded-full bg-gray-200 animate-pulse" />
          </div>

          <div className="p-6 flex flex-col gap-5">

            {/* Balance card skeleton */}
            <div className="bg-slate-900 rounded-xl p-5 relative overflow-hidden">
              <div className="absolute -top-10 -right-10 w-32 h-32 rounded-full bg-blue-900 opacity-50" />
              <div className="h-3 w-24 bg-slate-700 rounded animate-pulse mb-2" />
              <div className="h-9 w-40 bg-slate-700 rounded animate-pulse mb-4" />
              <div className="flex gap-2">
                <div className="h-7 w-24 bg-slate-700 rounded-lg animate-pulse" />
                <div className="h-7 w-24 bg-slate-700 rounded-lg animate-pulse" />
                <div className="h-7 w-24 bg-slate-700 rounded-lg animate-pulse" />
              </div>
            </div>

            {/* CBU y Alias skeleton */}
            <div className="bg-blue-50 rounded-xl border border-blue-100 p-4 flex flex-col gap-3">
              <div className="h-3 w-28 bg-blue-200 rounded animate-pulse" />
              <div className="flex justify-between items-center">
                <div className="flex flex-col gap-1">
                  <div className="h-3 w-8 bg-blue-200 rounded animate-pulse" />
                  <div className="h-4 w-48 bg-blue-200 rounded animate-pulse" />
                </div>
                <div className="h-3 w-10 bg-blue-200 rounded animate-pulse" />
              </div>
              <div className="h-px bg-blue-100" />
              <div className="flex justify-between items-center">
                <div className="flex flex-col gap-1">
                  <div className="h-3 w-8 bg-blue-200 rounded animate-pulse" />
                  <div className="h-4 w-32 bg-blue-200 rounded animate-pulse" />
                </div>
                <div className="h-3 w-10 bg-blue-200 rounded animate-pulse" />
              </div>
            </div>

            {/* Historial skeleton */}
            <div className="flex flex-col">
              <div className="h-3 w-32 bg-gray-200 rounded animate-pulse mb-3" />
              <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="flex justify-between items-center px-4 py-3 border-b border-gray-50 last:border-0">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 bg-gray-200 rounded-lg animate-pulse" />
                      <div className="flex flex-col gap-1">
                        <div className="h-3 w-28 bg-gray-200 rounded animate-pulse" />
                        <div className="h-3 w-20 bg-gray-100 rounded animate-pulse" />
                      </div>
                    </div>
                    <div className="h-3 w-16 bg-gray-200 rounded animate-pulse" />
                  </div>
                ))}
              </div>
            </div>

          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="flex min-h-screen">

      {/* Sidebar */}
      <div className="w-52 bg-slate-900 flex flex-col justify-between py-7 px-5 relative overflow-hidden shrink-0">
        <div className="absolute -top-14 -right-14 w-40 h-40 rounded-full bg-blue-900 opacity-35" />
        <div className="absolute -bottom-10 -left-10 w-28 h-28 rounded-full bg-blue-900 opacity-25" />

        <div className="z-10">
          {/* Brand */}
          <div className="mb-8">
            <NovaLogo />
          </div>

          {/* Nav */}
          <nav className="flex flex-col gap-1">
            <NavItem active path="/dashboard" label="Inicio"
              icon="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            <NavItem path="/transfer" label="Transferir"
              icon="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
            <NavItem path="/deposit" label="Depositar"
              icon="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            <NavItem path="/withdraw" label="Retirar"
              icon="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
          </nav>
        </div>

        <div
          onClick={handleLogout}
          className="z-10 flex items-center gap-2 px-3 py-2 rounded-lg cursor-pointer text-slate-600 text-sm hover:text-red-400 hover:bg-slate-800 transition"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Cerrar sesión
        </div>
      </div>

      {/* Main */}
      <div className="flex-1 flex flex-col bg-gray-50">

        {/* Topbar */}
        <div className="bg-white border-b border-gray-100 px-6 py-4 flex justify-between items-center">
          <h1 className="text-base font-medium text-gray-900">
            Hola, {user?.firstName || ''} !
          </h1>
          <p className="text-xs text-gray-400 mt-0.5">{user?.email || '...'}</p>
          <div className="w-8 h-8 rounded-full bg-blue-900 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4 text-blue-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
        </div>

        <div className="p-6 flex flex-col gap-5">

          {/* Balance card */}
          <div className="bg-slate-900 rounded-xl p-5 relative overflow-hidden">
            <div className="absolute -top-10 -right-10 w-32 h-32 rounded-full bg-blue-900 opacity-50" />
            <div className="absolute -bottom-8 right-20 w-20 h-20 rounded-full bg-blue-600 opacity-15" />
            <p className="text-slate-500 text-xs relative z-10">Saldo disponible</p>
            <div className="flex items-center gap-2 mt-1 mb-4">
              <p className="text-white text-3xl font-medium relative z-10 flex items-start">
                {showBalance ? (
                  balance !== null ? (
                    <>
                      <span>${Math.floor(Number(balance)).toLocaleString('es-AR')}</span>
                      <span className="text-base font-medium text-slate-400 mt-1 ml-1">
                        {(Number(balance) % 1).toFixed(2).slice(2)}
                      </span>
                    </>
                  ) : '...'
                ) : (
                  <span>$***</span>
                )}
              </p>

              <button
                onClick={() => setShowBalance(!showBalance)}
                className="text-slate-500 hover:text-slate-300 transition cursor-pointer mt-1"
              >
                {showBalance ? (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                ) : (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.477 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                  </svg>
                )}
              </button>
            </div>
            <div className="flex gap-2 relative z-10">
              <button
                onClick={() => navigate('/deposit')}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-xs font-medium transition cursor-pointer"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
                Depositar
              </button>
              <button
                onClick={() => navigate('/transfer')}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-700 hover:bg-slate-600 text-slate-300 rounded-lg text-xs font-medium transition cursor-pointer"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
                </svg>
                Transferir
              </button>
              <button
                onClick={() => navigate('/withdraw')}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-700 hover:bg-slate-600 text-slate-300 rounded-lg text-xs font-medium transition cursor-pointer"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                </svg>
                Retirar
              </button>
            </div>
          </div>

          {/* CBU y Alias */}
          <div className="bg-blue-50 rounded-xl border border-blue-100 p-4 flex flex-col gap-3">
            <p className="text-xs font-medium text-blue-400">Tus datos bancarios</p>

            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-blue-300 mb-0.5">CBU</p>
                <p className="text-sm font-mono text-blue-900">{user?.cbu || '...'}</p>
              </div>
              <button
                onClick={() => handleCopy(user?.cbu, 'cbu')}
                className="text-xs text-blue-500 hover:text-blue-700 transition cursor-pointer"
              >
                {copiedField === 'cbu' ? '✓ Copiado' : 'Copiar'}
              </button>
            </div>

            <div className="h-px bg-blue-100" />

            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-blue-300 mb-0.5">Alias</p>
                <p className="text-sm font-mono text-blue-900">{user?.alias || '...'}</p>
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => handleCopy(user?.alias, 'alias')}
                  className="text-xs text-blue-500 hover:text-blue-700 transition cursor-pointer"
                >
                  {copiedField === 'alias' ? '✓ Copiado' : 'Copiar'}
                </button>
                <button
                  onClick={() => navigate('/profile')}
                  className="text-xs text-blue-300 hover:text-blue-500 transition cursor-pointer"
                >
                  Editar
                </button>
              </div>
            </div>
          </div>

          {/* Historial */}
          <div className="flex flex-col">
            <p className="text-xs font-medium text-gray-400 mb-3">Últimos movimientos</p>

            {historyError && (
              <div className="px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600 mb-3">
                {historyError}
              </div>
            )}

            {/* Contenedor de altura fija que "reserva" el espacio */}
            <div style={{ minHeight: '245px' }} className="flex flex-col justify-between">
              
              <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
                {history.length === 0 ? (
                  <p className="text-sm text-gray-400 text-center py-8">No hay transacciones aún.</p>
                ) : (
                  history.map((t) => {
                    const { bg, color, icon } = getIconConfig(t)
                    return (
                      <div key={t.id} className="flex justify-between items-center px-4 py-3 border-b border-gray-50 last:border-0">
                        <div className="flex items-center gap-3">
                          <div className={`w-8 h-8 ${bg} rounded-lg flex items-center justify-center shrink-0`}>
                            <svg xmlns="http://www.w3.org/2000/svg" className={`w-4 h-4 ${color}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                              <path strokeLinecap="round" strokeLinejoin="round" d={icon} />
                            </svg>
                          </div>
                          <div>
                            <p className="text-sm font-medium text-gray-800">{getLabel(t)}</p>
                            <p className="text-xs text-gray-400">{formatDate(t.createdAt)}</p>
                          </div>
                        </div>
                        <span className={`text-sm font-medium ${getAmountColor(t)}`}>
                          {getAmountPrefix(t)}${Number(t.amount).toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                        </span>
                      </div>
                    )
                  })
                )}
              </div>

              {/* Botones siempre al fondo del contenedor */}
              <div className="flex items-center justify-center gap-4 mt-5">
                <button
                  disabled={page === 0}
                  onClick={() => setPage(page - 1)}
                  className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-sm font-medium transition disabled:bg-blue-300 disabled:cursor-not-allowed cursor-pointer"
                >
                  ← Anterior
                </button>

                <div className="px-4 py-2 bg-slate-900 rounded-lg">
                  <span className="text-sm font-medium text-white">{page + 1}</span>
                  <span className="text-sm text-slate-400 mx-1">/</span>
                  <span className="text-sm text-slate-300">{totalPages}</span>
                </div>

                <button
                  disabled={page + 1 >= totalPages}
                  onClick={() => setPage(page + 1)}
                  className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-sm font-medium transition disabled:bg-blue-300 disabled:cursor-not-allowed cursor-pointer"
                >
                  Siguiente →
                </button>
              </div>

            </div>
          </div>

        </div>
      </div>
    </div>
  )
}

export default Dashboard