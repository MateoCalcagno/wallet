import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useDashboard } from '../hooks/useDashboard'
import { useTransactionHistory } from '../hooks/useTransactionHistory'
import { getIconConfig, getLabel, getAmountColor, getAmountPrefix, formatDate } from '../utils/transactionHelpers'

function Dashboard() {
  const { user, balance, isLoading, handleLogout } = useDashboard()
  const { history, page, setPage, totalPages, historyError } = useTransactionHistory()
  const [copiedField, setCopiedField] = useState(null)
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
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
          <p className="text-sm text-gray-400">Cargando tu cuenta...</p>
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
          <div className="flex items-center gap-2 mb-8">
            <div className="w-8 h-8 bg-blue-500 rounded-lg flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
            </div>
            <span className="text-white text-sm font-medium">Wallet</span>
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
            <p className="text-white text-3xl font-medium mt-1 mb-4 relative z-10">
              ${balance !== null ? Number(balance).toFixed(2) : '...'}
            </p>
            <div className="flex gap-2 relative z-10">
              <button
                onClick={() => navigate('/deposit')}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-xs font-medium transition"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
                Depositar
              </button>
              <button
                onClick={() => navigate('/transfer')}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-700 hover:bg-slate-600 text-slate-300 rounded-lg text-xs font-medium transition"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
                </svg>
                Transferir
              </button>
              <button
                onClick={() => navigate('/withdraw')}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-700 hover:bg-slate-600 text-slate-300 rounded-lg text-xs font-medium transition"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                </svg>
                Retirar
              </button>
            </div>
          </div>

          {/* CBU y Alias */}
          <div className="bg-white rounded-xl border border-gray-100 p-4 flex flex-col gap-3">
            <p className="text-xs font-medium text-gray-400">Tus datos bancarios</p>

            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-400 mb-0.5">CBU</p>
                <p className="text-sm font-mono text-gray-800">{user?.cbu || '...'}</p>
              </div>
              <button
                onClick={() => handleCopy(user?.cbu, 'cbu')}
                className="text-xs text-blue-500 hover:text-blue-700 transition"
              >
                {copiedField === 'cbu' ? '✓ Copiado' : 'Copiar'}
              </button>
            </div>

            <div className="h-px bg-gray-50" />

            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-400 mb-0.5">Alias</p>
                <p className="text-sm font-mono text-gray-800">{user?.alias || '...'}</p>
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => handleCopy(user?.alias, 'alias')}
                  className="text-xs text-blue-500 hover:text-blue-700 transition"
                >
                  {copiedField === 'alias' ? '✓ Copiado' : 'Copiar'}
                </button>
                <button
                  onClick={() => navigate('/profile')}
                  className="text-xs text-gray-400 hover:text-gray-600 transition"
                >
                  Editar
                </button>
              </div>
            </div>
          </div>

          {/* Historial */}
          <div>
            <p className="text-xs font-medium text-gray-400 mb-3">Últimos movimientos</p>

            {historyError && (
              <div className="px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600 mb-3">
                {historyError}
              </div>
            )}

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
                        {getAmountPrefix(t)}${Number(t.amount).toFixed(2)}
                      </span>
                    </div>
                  )
                })
              )}
            </div>
            <div className="flex items-center justify-center gap-4 mt-5">

              <button
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
                className="
                  px-4 py-2
                  bg-blue-500
                  hover:bg-blue-600
                  text-white
                  rounded-lg
                  text-sm
                  font-medium
                  transition
                  disabled:bg-blue-300
                  disabled:cursor-not-allowed
                "
              >
                ← Anterior
              </button>

              <div className="px-4 py-2 bg-slate-900 rounded-lg">
                <span className="text-sm font-medium text-white">
                  {page + 1}
                </span>
                <span className="text-sm text-slate-400 mx-1">
                  /
                </span>
                <span className="text-sm text-slate-300">
                  {totalPages}
                </span>
              </div>

              <button
                disabled={page + 1 >= totalPages}
                onClick={() => setPage(page + 1)}
                className="
                  px-4 py-2
                  bg-blue-500
                  hover:bg-blue-600
                  text-white
                  rounded-lg
                  text-sm
                  font-medium
                  transition
                  disabled:bg-blue-300
                  disabled:cursor-not-allowed
                "
              >
                Siguiente →
              </button>

            </div>
          </div>

        </div>
      </div>
    </div>
  )
}

export default Dashboard