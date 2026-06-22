import { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useDashboard } from '../hooks/useDashboard'
import { useTransactionHistory } from '../hooks/useTransactionHistory'
import { getIconConfig, getLabel, getAmountColor, getAmountPrefix, formatDate } from '../utils/transactionHelpers'
import OnboardingTour from '../components/OnboardingTour'
import { useTour } from '../hooks/useTour'
import NovaLogo from '../components/NovaLogo'
import AppLayout from '../components/AppLayout'

function Dashboard() {
  const { user, balance, isLoading, handleLogout } = useDashboard()
  const { history, historyError } = useTransactionHistory({ size: 3 })
  const { isOpen, step, startTour, endTour, nextStep } = useTour()
  const [copiedField, setCopiedField] = useState(null)
  const [showBalance, setShowBalance] = useState(true)
  const navigate = useNavigate()

  const refNav      = useRef(null)
  const refBalance  = useRef(null)
  const refActions  = useRef(null)
  const refCbu      = useRef(null)
  const refHistory  = useRef(null)
  const refHelpBtn  = useRef(null)
  const refHeader = useRef(null)
  const refNavHome         = useRef(null)
  const refNavMovimientos  = useRef(null)
  const refNavEstadisticas = useRef(null)

  const tourSteps = [
    { ref: refNavHome,         title: 'Inicio',         desc: 'Tu pantalla principal: saldo, acciones rápidas y últimos movimientos.',         placement: 'right', padding: 6 },
    { ref: refNavMovimientos,  title: 'Movimientos',    desc: 'El historial completo de todo lo que entra y sale de tu cuenta.',               placement: 'right', padding: 6 },
    { ref: refNavEstadisticas, title: 'Estadísticas',   desc: 'Gráficos y resúmenes para entender cómo usás tu plata.',                       placement: 'right', padding: 6 },
    { ref: refBalance, title: 'Tu saldo disponible',  desc: 'Acá ves cuánto dinero tenés. Podés ocultarlo con el ícono del ojo, o ver el historial completo con la flecha.',  placement: 'bottom', padding: 6 },
    { ref: refActions, title: 'Acciones rápidas',     desc: 'Depositá, transferí o retirá con un toque.',                                               placement: 'bottom', padding: 6 },
    { ref: refCbu,     title: 'Tus datos bancarios',  desc: 'Tu CBU y alias para recibir transferencias. Copiálos o editá tu alias desde acá.',         placement: 'top',    padding: 6 },
    { ref: refHistory, title: 'Últimos movimientos',  desc: 'Las últimas transacciones de tu cuenta. Tocá "Ver todos" para el historial completo.',   placement: 'top',    padding: 6 },
    { ref: refHelpBtn, title: '¡Ya sabés todo!',      desc: 'Si querés volver a ver este tour, tocá el botón "?" de arriba a la derecha cuando quieras.', placement: 'bottom', padding: 6 },
  ]

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
        <div className="w-52 bg-slate-900 flex flex-col justify-between py-7 px-5 relative overflow-hidden shrink-0">
          <div className="absolute -top-14 -right-14 w-40 h-40 rounded-full bg-blue-900 opacity-35" />
          <div className="absolute -bottom-10 -left-10 w-28 h-28 rounded-full bg-blue-900 opacity-25" />
          <div className="z-10">
            <div className="mb-8"><NovaLogo /></div>
            <div className="flex flex-col gap-3">
              {[...Array(2)].map((_, i) => (
                <div key={i} className="h-8 bg-slate-800 rounded-lg animate-pulse" />
              ))}
            </div>
          </div>
        </div>
        <div className="flex-1 flex flex-col bg-gray-50">
          <div className="bg-white border-b border-gray-100 px-6 py-4 flex justify-between items-center">
            <div className="h-4 w-28 bg-gray-200 rounded animate-pulse" />
            <div className="h-3 w-36 bg-gray-200 rounded animate-pulse" />
            <div className="w-8 h-8 rounded-full bg-gray-200 animate-pulse" />
          </div>
          <div className="p-5 flex flex-col gap-4">

            {/* Balance card skeleton */}
            <div className="bg-slate-900 rounded-xl p-6 relative overflow-hidden">
              <div className="absolute -right-6 -top-6 h-20 w-20 rounded-full bg-blue-700 opacity-25" />
              {/* Label + badge */}
              <div className="flex items-center justify-between mb-1">
                <div className="h-3 w-24 bg-slate-700 rounded animate-pulse" />
                <div className="h-4 w-16 bg-slate-700 rounded-full animate-pulse" />
              </div>
              {/* Saldo + ojo + chevron */}
              <div className="flex items-center gap-3 mb-4">
                <div className="h-11 w-48 bg-slate-700 rounded animate-pulse" />
                <div className="w-4 h-4 bg-slate-700 rounded animate-pulse" />
                <div className="w-10 h-10 bg-slate-700 rounded animate-pulse ml-auto" />
              </div>
              <div className="h-px bg-slate-700/60 mb-2.5" />
              {/* Botones */}
              <div className="flex gap-1.5">
                <div className="h-7 flex-1 bg-blue-900 rounded-lg animate-pulse" />
                <div className="h-7 flex-1 bg-slate-800 rounded-lg animate-pulse" />
                <div className="h-7 flex-1 bg-slate-800 rounded-lg animate-pulse" />
              </div>
            </div>

            {/* CBU skeleton */}
            <div className="bg-blue-50 rounded-xl border border-blue-100 p-4 flex flex-col gap-2.5">
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
              <div className="h-3 w-32 bg-gray-200 rounded animate-pulse mb-3 ml-4" />
              <div className="bg-blue-50 rounded-xl border border-blue-100 overflow-hidden">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="flex justify-between items-center px-4 py-3 border-b border-blue-100/60 last:border-0">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 bg-blue-200 rounded-lg animate-pulse" />
                      <div className="flex flex-col gap-1">
                        <div className="h-3 w-28 bg-blue-200 rounded animate-pulse" />
                        <div className="h-3 w-20 bg-blue-100 rounded animate-pulse" />
                      </div>
                    </div>
                    <div className="h-3 w-16 bg-blue-200 rounded animate-pulse" />
                  </div>
                ))}
              </div>
              <div className="h-4 w-44 bg-gray-200 rounded animate-pulse mt-6 mx-auto" />
            </div>

          </div>
        </div>
      </div>
    )
  }

  return (
    <AppLayout refNav={refNav} refNavHome={refNavHome} refNavMovimientos={refNavMovimientos} refNavEstadisticas={refNavEstadisticas} refHelpBtn={refHelpBtn} onStartTour={startTour}>
      <div className="p-5 flex flex-col gap-4">

        {/* Balance card */}
        <div ref={refBalance} className="relative w-full max-w-full overflow-hidden rounded-xl bg-slate-900 p-6 isolate">
          <div className="absolute -right-6 -top-6 -z-10 h-20 w-20 rounded-full bg-blue-700 opacity-25 pointer-events-none" />
          <div className="absolute -bottom-4 right-14 -z-10 h-12 w-12 rounded-full bg-blue-500 opacity-10 pointer-events-none" />
          <div
            className="absolute top-0 left-0 w-full h-full -z-10 pointer-events-none"
            style={{ background: 'linear-gradient(135deg, rgba(59,130,246,0.07) 0%, transparent 60%)' }}
          />

          <div className="relative z-10 w-full">

            {/* Fila superior: label + badge ACTIVO */}
            <div className="flex items-center justify-between mb-1 ml-1 mr-0.5">
              <p className="text-slate-500 text-[11px] select-none">Saldo disponible</p>
              <span className="flex items-center gap-1 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-[10px] font-medium px-2 py-0.5 rounded-full">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse inline-block" />
                ACTIVO
              </span>
            </div>

            {/* Saldo + ojo */}
            <div className="flex items-center gap-3 mb-4 max-w-full overflow-hidden">
              <div className="flex items-baseline leading-none overflow-hidden select-none">
                {showBalance ? (
                  balance !== null ? (
                    <>
                      <span className="text-slate-400 text-[20px] font-normal mr-0.5">$</span>
                      <span className="text-white text-[44px] font-semibold tracking-tight truncate">
                        {Math.floor(Number(balance)).toLocaleString('es-AR')}
                      </span>
                      <span className="text-slate-500 text-[14px] font-medium align-super self-start mt-1 ml-0.5 inline-block">
                        {String(Math.round((Number(balance) % 1) * 100)).padStart(2, '0')}
                      </span>
                    </>
                  ) : <span className="text-white text-[44px] font-semibold">...</span>
                ) : (
                  <>
                    <span className="text-slate-400 text-[20px] font-normal mr-0.5">$</span>
                    <span className="text-white text-[44px] font-semibold tracking-tight">***</span>
                  </>
                )}
              </div>
              <button
                onClick={() => setShowBalance(!showBalance)}
                className="text-slate-500 hover:text-slate-300 transition cursor-pointer self-center mt-1 shrink-0"
              >
                {showBalance ? (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                ) : (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.477 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                  </svg>
                )}
              </button>
              <button
                onClick={() => navigate('/history')}
                className="text-white hover:text-slate-300 transition cursor-pointer self-center mt-1 shrink-0 ml-auto"
                aria-label="Ver historial"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>

            <div className="h-px bg-slate-700/60 mb-2.5" />

            {/* Acciones */}
            <div ref={refActions} className="flex gap-1.5 w-full">
              <button onClick={() => navigate('/deposit')}
                className="flex-1 flex items-center justify-center gap-1 py-1 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-[11px] font-medium transition cursor-pointer min-w-0">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
                <span className="truncate">Depositar</span>
              </button>

              <button onClick={() => navigate('/transfer')}
                className="flex flex-1 items-center justify-center gap-1 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-[11px] font-medium transition cursor-pointer border border-slate-600/30 min-w-0">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
                </svg>
                <span className="truncate">Transferir</span>
              </button>

              <button onClick={() => navigate('/withdraw')}
                className="flex flex-1 items-center justify-center gap-1 py-1 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-[11px] font-medium transition cursor-pointer border border-slate-600/30 min-w-0">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                </svg>
                <span className="truncate">Retirar</span>
              </button>
            </div>
          </div>
        </div>

        {/* CBU y Alias */}
        <div ref={refCbu} className="bg-blue-50 rounded-xl border border-slate-900 p-4 flex flex-col gap-2.5">
          <p className="text-xs font-medium text-gray-400">Tus datos bancarios</p>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-gray-400 mb-0.5">CBU</p>
              <p className="text-sm font-mono text-gray-800">{user?.cbu || '...'}</p>
            </div>
            <button onClick={() => handleCopy(user?.cbu, 'cbu')} className="text-xs text-blue-500 hover:text-blue-700 transition cursor-pointer">
              {copiedField === 'cbu' ? '✓ Copiado' : 'Copiar'}
            </button>
          </div>
          <div className="h-px bg-gray-100" />
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-gray-400 mb-0.5">Alias</p>
              <p className="text-sm font-mono text-gray-800">{user?.alias || '...'}</p>
            </div>
            <div className="flex gap-2">
              <button onClick={() => handleCopy(user?.alias, 'alias')} className="text-xs text-blue-500 hover:text-blue-700 transition cursor-pointer">
                {copiedField === 'alias' ? '✓ Copiado' : 'Copiar'}
              </button>
              <button onClick={() => navigate('/profile')} className="text-xs text-gray-400 hover:text-blue-500 transition cursor-pointer">
                Editar
              </button>
            </div>
          </div>
        </div>

        {/* Historial */}
        <div ref={refHistory} className="flex flex-col">
          <p className="text-xs font-medium text-gray-400 mb-3 pl-4">Últimos movimientos</p>
          {historyError && (
            <div className="px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600 mb-3">
              {historyError}
            </div>
          )}
          <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
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
          <button onClick={() => navigate('/history')} className="mt-6 text-sm font-medium text-slate-900 hover:text-blue-500 transition cursor-pointer text-center mx-auto block">
            Ver todos los movimientos →
          </button>
        </div>

      </div>

      <OnboardingTour
        steps={tourSteps}
        isOpen={isOpen}
        currentStep={step}
        onNext={() => nextStep(tourSteps.length)}
        onEnd={endTour}
      />
    </AppLayout>
  )
}

export default Dashboard