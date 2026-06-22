import { useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTransactionHistory } from '../hooks/useTransactionHistory'
import AppLayout from '../components/AppLayout'

function Statistics() {
  const { history, isLoading } = useTransactionHistory({ size: 100 })
  const navigate = useNavigate()

  const stats = useMemo(() => {
    const now = new Date()
    const currentMonth = now.getMonth()
    const currentYear = now.getFullYear()

    const thisMonth = history.filter((t) => {
      const d = new Date(t.createdAt)
      return d.getMonth() === currentMonth && d.getFullYear() === currentYear
    })

    const ingresos = thisMonth
      .filter((t) => t.type === 'DEPOSIT' || t.direction === 'IN' || t.type === 'TRANSFER_IN')
      .reduce((acc, t) => acc + Number(t.amount), 0)

    const gastos = thisMonth
      .filter((t) => t.type === 'WITHDRAWAL' || t.direction === 'OUT' || t.type === 'TRANSFER_OUT')
      .reduce((acc, t) => acc + Number(t.amount), 0)

    const balance = ingresos - gastos
    const total = ingresos + gastos
    const ingresosPercent = total > 0 ? (ingresos / total) * 100 : 50
    const gastosPercent = total > 0 ? (gastos / total) * 100 : 50

    const byDay = {}
    thisMonth.forEach((t) => {
      const day = new Date(t.createdAt).getDate()
      if (!byDay[day]) byDay[day] = { ingresos: 0, gastos: 0 }
      const isIngreso = t.type === 'DEPOSIT' || t.direction === 'IN' || t.type === 'TRANSFER_IN'
      if (isIngreso) byDay[day].ingresos += Number(t.amount)
      else byDay[day].gastos += Number(t.amount)
    })

    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate()
    const days = Array.from({ length: daysInMonth }, (_, i) => ({
      day: i + 1,
      ingresos: byDay[i + 1]?.ingresos || 0,
      gastos: byDay[i + 1]?.gastos || 0,
    }))

    const maxVal = Math.max(...days.map((d) => Math.max(d.ingresos, d.gastos)), 1)

    return { ingresos, gastos, balance, ingresosPercent, gastosPercent, days, maxVal, total }
  }, [history])

  const monthName = new Date().toLocaleString('es-AR', { month: 'long', year: 'numeric' })

  const fmt = (n) =>
    '$' + Math.abs(n).toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

  return (
    <AppLayout>
      <div className="p-5 flex flex-col gap-3">

        {/* Header */}
        <div className="pb-3 border-b border-gray-100 pl-3">
          <button
            onClick={() => navigate('/dashboard')}
            className="flex items-center gap-1.5 text-xs text-gray-400 font-medium hover:text-blue-400 transition cursor-pointer mb-4"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
            </svg>
            Volver a Inicio
          </button>
          <h1 className="text-4xl font-bold text-slate-800 leading-none tracking-tight">Estadísticas</h1>
          <p className="text-sm text-blue-400 mt-1 capitalize">{monthName}</p>
        </div>

        {isLoading ? (
  <div className="flex flex-col gap-3">

    {/* Balance del mes skeleton */}
    <div className="bg-white rounded-xl p-4 border border-gray-100">
      <div className="h-3 w-24 bg-gray-200 rounded animate-pulse mb-2" />
      <div className="h-8 w-44 bg-gray-200 rounded animate-pulse mb-3" />
      <div className="h-px bg-gray-100 mb-3" />
      <div className="h-1.5 w-full bg-gray-100 rounded-full animate-pulse mb-2" />
      <div className="flex justify-between">
        <div className="h-3 w-20 bg-gray-100 rounded animate-pulse" />
        <div className="h-3 w-20 bg-gray-100 rounded animate-pulse" />
      </div>
    </div>

    {/* Cards ingresos / gastos skeleton */}
    <div className="grid grid-cols-2 gap-3">
      {[0, 1].map((i) => (
        <div key={i} className="bg-blue-50 border border-blue-100 rounded-xl p-3">
          <div className="flex items-center gap-2 mb-1.5">
            <div className="w-7 h-7 bg-blue-100 rounded-lg animate-pulse" />
            <div className="h-3 w-14 bg-blue-100 rounded animate-pulse" />
          </div>
          <div className="h-6 w-28 bg-blue-100 rounded animate-pulse" />
        </div>
      ))}
    </div>

    {/* Gráfico skeleton */}
    <div className="bg-blue-50 border border-blue-100 rounded-xl p-3">
      <div className="h-3 w-24 bg-blue-100 rounded animate-pulse mb-3" />
      <div className="flex items-end gap-0.5 h-28">
        {[...Array(10)].map((_, i) => (
          <div
            key={i}
            className="flex-1 bg-blue-100 rounded-t animate-pulse"
            style={{ height: `${20 + Math.random() * 60}%` }}
          />
        ))}
      </div>
      <div className="flex justify-between mt-1.5">
        <div className="h-3 w-3 bg-blue-100 rounded animate-pulse" />
        <div className="h-3 w-3 bg-blue-100 rounded animate-pulse" />
        <div className="h-3 w-3 bg-blue-100 rounded animate-pulse" />
      </div>
      <div className="flex gap-4 mt-2">
        <div className="h-3 w-16 bg-blue-100 rounded animate-pulse" />
        <div className="h-3 w-16 bg-blue-100 rounded animate-pulse" />
      </div>
    </div>

  </div>
) : (
          <>
            {/* Balance del mes */}
            <div className="bg-white rounded-xl p-4 border border-gray-100 shadow-sm relative overflow-hidden">
              <div className="absolute top-0 right-0 w-24 h-24 rounded-full bg-blue-50 -translate-y-8 translate-x-8" />

              <div className="relative z-10">
                <p className="text-xs text-gray-400 mb-1">Balance del mes</p>
                <p className={`text-3xl font-medium mt-1 mb-3 ${stats.balance >= 0 ? 'text-slate-800' : 'text-red-500'}`}>
                  {stats.balance >= 0 ? '+' : '-'}{fmt(stats.balance)}
                </p>

                <div className="h-px bg-gray-100 mb-3" />

                <div className="flex rounded-full overflow-hidden h-1.5 mb-2">
                  <div className="bg-blue-400 transition-all duration-700" style={{ width: `${stats.ingresosPercent}%` }} />
                  <div className="bg-red-300 transition-all duration-700" style={{ width: `${stats.gastosPercent}%` }} />
                </div>
                <div className="flex justify-between">
                  <span className="text-xs text-blue-400">Ingresos {stats.ingresosPercent.toFixed(0)}%</span>
                  <span className="text-xs text-red-400">Gastos {stats.gastosPercent.toFixed(0)}%</span>
                </div>
              </div>
            </div>

            {/* Cards ingresos / gastos */}
            <div className="grid grid-cols-2 gap-3">
              <div className="bg-blue-50 border border-blue-100 rounded-xl p-3">
                <div className="flex items-center gap-2 mb-1.5">
                  <div className="w-7 h-7 bg-blue-100 rounded-lg flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                  </div>
                  <p className="text-xs text-blue-400">Ingresos</p>
                </div>
                <p className="text-lg font-medium text-blue-900">{fmt(stats.ingresos)}</p>
              </div>

              <div className="bg-blue-50 border border-blue-100 rounded-xl p-3">
                <div className="flex items-center gap-2 mb-1.5">
                  <div className="w-7 h-7 bg-red-100 rounded-lg flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                    </svg>
                  </div>
                  <p className="text-xs text-red-400">Gastos</p>
                </div>
                <p className="text-lg font-medium text-blue-900">{fmt(stats.gastos)}</p>
              </div>
            </div>

            {/* Gráfico de barras por día */}
            <div className="bg-blue-50 border border-blue-100 rounded-xl p-3">
              <p className="text-xs font-medium text-blue-400 mb-3">Actividad diaria</p>
              <div className="flex items-end gap-0.5 h-28">
                {stats.days.map((d) => (
                  <div key={d.day} className="flex-1 flex flex-col items-center gap-0.5 h-full justify-end group relative">
                    {(d.ingresos > 0 || d.gastos > 0) && (
                      <div className="absolute bottom-full mb-1 left-1/2 -translate-x-1/2 hidden group-hover:flex flex-col items-center z-10 pointer-events-none">
                        <div className="bg-slate-900 text-white text-xs rounded-lg px-2 py-1.5 whitespace-nowrap text-center">
                          <span className="block text-blue-400">+{fmt(d.ingresos)}</span>
                          <span className="block text-red-400">-{fmt(d.gastos)}</span>
                        </div>
                        <div className="w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-slate-900" />
                      </div>
                    )}
                    {d.ingresos > 0 && (
                      <div
                        className="w-full bg-blue-200 hover:bg-blue-400 rounded-t transition-colors"
                        style={{ height: `${(d.ingresos / stats.maxVal) * 100}%`, minHeight: '2px' }}
                      />
                    )}
                    {d.gastos > 0 && (
                      <div
                        className="w-full bg-red-200 hover:bg-red-400 rounded-t transition-colors"
                        style={{ height: `${(d.gastos / stats.maxVal) * 100}%`, minHeight: '2px' }}
                      />
                    )}
                    {d.ingresos === 0 && d.gastos === 0 && (
                      <div className="w-full bg-blue-100 rounded-t" style={{ height: '2px' }} />
                    )}
                  </div>
                ))}
              </div>
              <div className="flex justify-between mt-1.5">
                <span className="text-xs text-blue-300">1</span>
                <span className="text-xs text-blue-300">{Math.ceil(stats.days.length / 2)}</span>
                <span className="text-xs text-blue-300">{stats.days.length}</span>
              </div>
              <div className="flex gap-4 mt-2">
                <div className="flex items-center gap-1.5">
                  <div className="w-2.5 h-2.5 rounded-sm bg-blue-300" />
                  <span className="text-xs text-blue-400">Ingresos</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <div className="w-2.5 h-2.5 rounded-sm bg-red-300" />
                  <span className="text-xs text-red-400">Gastos</span>
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </AppLayout>
  )
}

export default Statistics